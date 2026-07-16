package com.hm.viscosityauto.utils.ota

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.hm.viscosityauto.utils.SerialManager
import com.hm.viscosityauto.utils.SerialPortManager

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



class OtaController(
    private val serial: SerialManager,
    private val firmware: ByteArray,
    private val fileName: String = "app.bin"
) {

    private val TAG = "OTAController"

    public var state by mutableStateOf(State.IDLE)
    private var index = 0

    private val ymodem = YModemEngine(serial)

    enum class State {
        IDLE,
        ENTER_OTA,
        WAIT_C,
        SEND_HEADER,
        SEND_DATA,
        SEND_EOT,
        WAIT_EMPTY,
        FINISH,
        FAILED
    }

    init {
        serial.setOtaCallback(object : OtaCallback {

            override fun onC() {
                handleC()
            }

            override fun onAck() {
//                handleAck()
            }

            override fun onNak() {
                retry()
            }

            override fun onCancel() {
            }
        })
    }

    // -------------------------
    // 启动OTA
    // -------------------------
    fun start() {
        Log.e(TAG, "start OTA")

        state = State.ENTER_OTA

        serial.enterOtaMode()

        sendEnterOtaCmd()
    }

    private fun sendEnterOtaCmd() {
        val cmd = "FAAF50A500000000EAAE"

        serial.writeImmediately(cmd)
        state = State.WAIT_C
    }

    // -------------------------
    // Bootloader 'C'
    // -------------------------
    private fun handleC() {
        Log.d(TAG, "recv C state=$state")

        when (state) {

            State.WAIT_C, State.ENTER_OTA -> {
                sendHeader()
            }

            State.SEND_HEADER -> {
              sendData()
            }

            State.SEND_DATA -> {
                sendEot()
            }

            State.SEND_EOT -> {
                sendEmptyHeader()
            }

            else -> {}
        }
    }

    // -------------------------
    // ACK处理
    // -------------------------
    private fun handleAck() {

        Log.d(TAG, "ACK state=$state")

        when (state) {

            State.SEND_HEADER -> {
                sendData()
            }

            State.SEND_DATA -> {
                sendEot()
            }

            State.SEND_EOT -> {
                state = State.WAIT_EMPTY
            }

            State.WAIT_EMPTY -> {
                finish()
            }

            else -> {}
        }
    }

    private fun retry() {
        Log.e(TAG, "NAK retry state=$state")

        when (state) {
            State.SEND_HEADER -> sendHeader()
            State.SEND_DATA -> sendData()
            State.SEND_EOT -> sendEot()
            else -> {}
        }
    }

    // -------------------------
    // Header
    // -------------------------
    private fun sendHeader() {
        Log.e(TAG, "send header")

        state = State.SEND_HEADER

        ymodem.sendHeader(fileName, firmware.size)


    }

    // -------------------------
    // Data
    // -------------------------
    private fun sendData() {
        Log.e(TAG, "send data")

        state = State.SEND_DATA

        index = ymodem.sendData(firmware)

    }

    // -------------------------
    // EOT
    // -------------------------
    private fun sendEot() {
        Log.e(TAG, "send EOT")

        state = State.SEND_EOT

        serial.writeImmediately("04")
    }

    // -------------------------
    // 空包
    // -------------------------
    private fun sendEmptyHeader() {
        Log.e(TAG, "send empty header")

        ymodem.sendEmptyHeader()

        finish()
    }

    // -------------------------
    // 完成
    // -------------------------
    private fun finish() {
        Log.e(TAG, "OTA FINISH")

        state = State.FINISH

        serial.exitOtaMode()
    }

    fun stop() {
        state = State.FAILED
        serial.exitOtaMode()
    }
}