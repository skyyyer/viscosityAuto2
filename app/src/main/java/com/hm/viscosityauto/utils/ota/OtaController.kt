package com.hm.viscosityauto.utils.ota

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.hm.viscosityauto.utils.SerialManager

interface OtaCallback {

    /**
     * Bootloader发送 'C' (0x43)
     * 表示请求 YMODEM CRC 模式开始
     */
    fun onC()

    /**
     * 收到 ACK (0x06)
     */
    fun onAck()

    /**
     * 收到 NAK (0x15)
     */
    fun onNak()

    /**
     * 可选：取消传输
     */
    fun onCancel()
}

//对外暴露状态
object OtaStatus {

    const val IDLE = 0

    const val ENTER_OTA = 1
    const val WAIT_BOOT = 2
    const val SEND_HEADER = 3
    const val SEND_DATA = 4
    const val SEND_EOT = 5
    const val SEND_EMPTY = 6
    const val WAIT_REBOOT = 7
    const val QUERY_VERSION = 8

    const val SUCCESS = 100

    const val FAIL = -1
    const val FAIL_RETRY = -2
    const val FAIL_CANCEL = -3
}

class OtaController(
    private val serial: SerialManager,
    private val firmware: ByteArray,
    private val fileName: String = "app.bin"
) : OtaCallback {

    companion object {
        private const val TAG = "OtaController"
        private const val CMD_ENTER_OTA = 0x50
        private const val CMD_GET_VERSION = 0x51
    }



    private val ymodem = YModemEngine()

    private var state = State.IDLE
    private var offset = 0
    private var retryCount = 0
    private var currentPacket: ByteArray? = null


    //对外暴露状态
    var otaStatus by mutableIntStateOf(OtaStatus.IDLE)
        private set
    var progress by mutableIntStateOf(0)
        private set

    private fun updateProgress(value: Int) {
        progress = value.coerceIn(0, 100)
    }


    enum class State {
        IDLE,
        WAIT_ENTER_ACK,
        WAIT_FIRST_C,
        WAIT_HEADER_ACK,
        WAIT_SECOND_C,
        WAIT_DATA_ACK,
        WAIT_EOT_NAK,
        WAIT_EOT_ACK,
        WAIT_EMPTY_C,
        WAIT_EMPTY_ACK,
        WAIT_REBOOT,
        WAIT_VERSION,
        SUCCESS,
        FAILED
    }
    private fun updateStatus(status: Int) {
        otaStatus = status
    }
    fun start() {
        if (firmware.isEmpty()) {
            fail("firmware empty")
            return
        }
        ymodem.reset()
        serial.setOtaCallback(this)
        serial.enterOtaMode()
        sendEnterOta()
        onEnterOtaAck()

        updateStatus(OtaStatus.ENTER_OTA)

    }

    private fun sendEnterOta() {
        val cmd = byteArrayOf(
            0xFA.toByte(), 0xAF.toByte(),
            0x50,
            0xA5.toByte(),
            0x00, 0x00, 0x00, 0x00,
            0xEA.toByte(), 0xAE.toByte()
        )
        state = State.WAIT_ENTER_ACK
        serial.writeBytes(cmd)
        Log.d(TAG, "send enter ota")
    }

    /**
     * APP阶段返回：
     * FA AF 50 A5 00 00 00 00 EA AE
     */
    fun onEnterOtaAck() {
        if (state != State.WAIT_ENTER_ACK) return
        state = State.WAIT_FIRST_C
        updateStatus(OtaStatus.WAIT_BOOT)

        Log.d(TAG, "wait bootloader C")
    }

    override fun onC() {
        Log.d(TAG, "recv C state=$state")
        when (state) {
            State.WAIT_FIRST_C -> {
                sendHeader()
            }
            State.WAIT_SECOND_C -> {
                sendNextPacket()
            }
            State.WAIT_EMPTY_C -> {
                sendEmptyPacket()
            }
            else -> {}
        }
    }

    private fun sendHeader() {
        currentPacket = ymodem.createHeaderPacket(fileName, firmware.size)
        state = State.WAIT_HEADER_ACK
        updateStatus(OtaStatus.SEND_HEADER)
        serial.writeBytes(currentPacket!!)
        Log.d(TAG, "send header")
    }

    override fun onAck() {
        Log.d(TAG, "recv ACK state=$state")

        when (state) {

            State.WAIT_HEADER_ACK -> {
                // 文件头ACK后，等待Bootloader第二个C
                state = State.WAIT_SECOND_C
            }

            State.WAIT_DATA_ACK -> {
                retryCount = 0

                if (offset >= firmware.size) {
                    sendEot()
                } else {
                    sendNextPacket()
                }
            }

            State.WAIT_EOT_ACK -> {
                state = State.WAIT_EMPTY_C
            }

            State.WAIT_EMPTY_ACK -> {
                state = State.WAIT_REBOOT
                waitDeviceRestart()
            }

            else -> {}
        }
    }

    override fun onNak() {
        Log.d(TAG, "recv NAK state=$state")

        when (state) {

            State.WAIT_DATA_ACK -> {
                retrySend()
            }

            State.WAIT_EOT_NAK -> {
                sendSecondEot()
            }

            else -> {}
        }
    }

    override fun onCancel() {
        fail("bootloader cancel")
        updateStatus(OtaStatus.FAIL_CANCEL)

    }

    private fun sendNextPacket() {
        if (offset >= firmware.size) {
            sendEot()
            return
        }
        updateStatus(OtaStatus.SEND_DATA)
        val totalPacket = (firmware.size + 1023) / 1024
        val packetIndex = offset / 1024 + 1

        updateProgress(packetIndex * 100 / totalPacket)

        currentPacket = ymodem.createDataPacket(firmware, offset)

        offset += minOf(
            1024,
            firmware.size - offset
        )

        state = State.WAIT_DATA_ACK
        serial.writeBytes(currentPacket!!)
        Log.d(TAG, "send data offset=$offset")
    }

    private fun retrySend() {
        if (++retryCount > 10) {
            fail("retry over")
            return
        }
        currentPacket?.let {
            serial.writeBytes(it)
        }
    }

    /**
     * 第一次EOT
     */
    private fun sendEot() {
        state = State.WAIT_EOT_NAK
        serial.writeBytes(byteArrayOf(0x04))
        Log.d(TAG, "send EOT 1")
    }

    /**
     * 第二次EOT
     */
    private fun sendSecondEot() {
        state = State.WAIT_EOT_ACK
        serial.writeBytes(byteArrayOf(0x04))
        Log.d(TAG, "send EOT 2")
    }

    private fun sendEmptyPacket() {
        currentPacket = ymodem.createEmptyPacket()
        state = State.WAIT_EMPTY_ACK
        serial.writeBytes(currentPacket!!)
        Log.d(TAG, "send empty packet")
    }

    private fun waitDeviceRestart() {
        Thread {
            try {
                Thread.sleep(2000)
                queryVersion()
            } catch (e: Exception) {
                fail(e.message ?: "wait restart error")
            }
        }.start()
    }

    private fun queryVersion() {
        val cmd = byteArrayOf(
            0xFA.toByte(), 0xAF.toByte(),
            CMD_GET_VERSION.toByte(),
            0xAA.toByte(),
            0x00, 0x00, 0x00, 0x00,
            0xEA.toByte(), 0xAE.toByte()
        )

        state = State.WAIT_VERSION
        serial.exitOtaMode()
        serial.writeBytes(cmd)
        updateStatus(OtaStatus.SUCCESS)

        Log.d(TAG, "query version")
    }

    private fun fail(msg: String) {
        Log.e(TAG, msg)
        state = State.FAILED
        updateStatus(OtaStatus.FAIL)

        serial.setOtaCallback(null)
        serial.exitOtaMode()
    }

    fun stop() {
        fail("manual stop")
    }

    fun getState(): State = state
}