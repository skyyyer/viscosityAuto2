package com.hm.viscosityauto.utils

import android.serialport.SerialPort
import android.util.Log
import com.hm.viscosityauto.utils.ota.OtaCallback
import com.hm.viscosityauto.vm.TestState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean


class SerialManager private constructor(
    private val devicePath: String,
    private val baudRate: Int
) {

    companion object {
        private const val TAG = "SerialPortManager"
        private const val BUFFER_SIZE = 64
        private const val SEND_INTERVAL = 50L


        const val CRC = "00"

        const val HEAD = "faaf"
        const val FOOT = "eaae"

        //读取缸内温度
        const val CMD_READ_T = "01"

        //介质控制
        const val MEDIUM_VALUE = "04"

        //照明控制
        const val CMD_LIGHT = "06"

        //加热控制
        const val CMD_SET_T = "07"

        //a通道设置
        const val A_CMD = "08"

        //b通道设置
        const val B_CMD = "09"

        //清洗时间
        const val CMD_SET_CLEAN_DURATION = "10"


        //进清洗液时间
        const val CMD_LIQUID_ENTER_DURATION = "11"

        //抽提时间
        const val CMD_EXTRACT_DURATION = "12"

        //抽提间隔
        const val CMD_EXTRACT_INTERVAL = "13"

        //A检测值
        const val A_VALUE = "14"

        //B检测值
        const val B_VALUE = "15"


        //传感器光强  查询
        const val SENSOR_LIGHT = "16"

        //AB 检测值上报
        const val AB_VALUE_UP = "17"

        //A上设定值 灵敏度
        const val A_UP_SET = "18"

        //A下设定值 灵敏度
        const val A_DOWN_SET = "19"

        //B上设定值 灵敏度
        const val B_UP_SET = "20"

        //B下设定值 灵敏度
        const val B_DOWN_SET = "21"

        //22	设置/查询	泵电机版本	D0=00 旧泵，D0=01 新泵	D0=当前泵版本
        const val PUMP_MOTOR_VER = "22"


        //电磁阀测试
        const val SV_TEST = "26"

        //电机测试
        const val MOTOR_SEN = "27"

        //测试模式
        const val DEBUG_MODE = "28"

        //A状态
        const val A_STATE = "29"

        //B状态
        const val B_STATE = "30"

        //加热状态
        const val HEATING_STATE = "31"

        //电机速度
        const val MOTOR_SPEED = "32"


        //排空电机速度
        const val EMPTY_MOTOR_SPEED = "33"

        //排空抽提时间
        const val EMPTY_EXTRACT_DURATION = "34"

        //排空抽提间隔
        const val EMPTY_EXTRACT_INTERVAL = "35"

        // 排空烘干时间
        const val EMPTY_DRYING_DURATION = "36"

        //清洗电机速度
        const val CLEAN_MOTOR_SPEED = "37"

        //清洗烘干时间
        const val CLEAN_DRYING_DURATION = "38"

        //泄压时间
        const val DECOM_P_DURATION = "39"

        //    51	查询	固件版本号	D0=AA	D0=主版本，D1=次版本，D2=修订，D3:D4=build
        const val FIRMWARW_VER = "51"


        @Volatile
        private var INSTANCE: SerialManager? = null

        fun getInstance(devicePath: String, baudRate: Int): SerialManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SerialManager(devicePath, baudRate).also { INSTANCE = it }
            }
        }

        fun getInstanceOrNull(): SerialManager? = INSTANCE

        fun closeInstance() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }


    private var serialPort: SerialPort? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val running = AtomicBoolean(false)

    private val sendChannel = Channel<ByteArray>(Channel.UNLIMITED)

    private val listeners = CopyOnWriteArrayList<OnDataReceivedListener>()

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState


    enum class ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        ERROR
    }


    interface OnDataReceivedListener {
        fun onTemperatureReceived(temperature: String) {}
        fun onLightStateReceived(state: Boolean) {}
        fun onHeatingState(state: Int) {}
        fun onADeviceState(state: Int, duration: Double) {}
        fun onBDeviceState(state: Int, duration: Double) {}
        fun onADetectedValue(up: Int, down: Int) {}
        fun onBDetectedValue(up: Int, down: Int) {}
        fun onSensorLightValue(aUp: Int, aDown: Int, bUp: Int, bDown: Int) {}
        fun onPumpMotor(version: Int) {}
    }


    fun addListener(listener: OnDataReceivedListener) {
        if (!listeners.contains(listener)) listeners.add(listener)
    }


    fun removeListener(listener: OnDataReceivedListener) {
        listeners.remove(listener)
    }


    fun clearListener() {
        listeners.clear()
    }


    @Synchronized
    fun initialize() {
        if (running.get()) return

        try {
            _connectionState.value = ConnectionState.CONNECTING

            serialPort = SerialPort(File(devicePath), baudRate)
            inputStream = serialPort?.inputStream
            outputStream = serialPort?.outputStream

            running.set(true)

            startReadingCoroutine()
            startSendingCoroutine()

            _connectionState.value = ConnectionState.CONNECTED

            Log.d(TAG, "serial open success")

        } catch (e: Exception) {
            Log.e(TAG, "open serial error", e)
            _connectionState.value = ConnectionState.ERROR
        }
    }
    private fun startReadingCoroutine() = scope.launch {
        val buffer = ByteArray(BUFFER_SIZE)

        while (running.get()) {
            try {
                val len = inputStream?.read(buffer) ?: 0

                if (len > 0) {
                    if (transferMode == TransferMode.OTA) {
                        otaStreamBuffer.write(buffer, 0, len)
                        // ✅ 2. 解析完整流
                        val data = otaStreamBuffer.toByteArray()
                        otaStreamBuffer.reset()
                        processOtaReceived(data, data.size)
                    } else {
                        processReceivedData(buffer, len)
                    }
                }

                delay(1)

            } catch (e: Exception) {
                if (running.get()) {
                    Log.e(TAG, "read error", e)
                }
                break
            }
        }
    }


    private fun startSendingCoroutine() = scope.launch {
        while (running.get()) {
            try {
                val data = sendChannel.receive()

                outputStream?.apply {
                    write(data)
                    flush()
                }

                delay(SEND_INTERVAL)

            } catch (e: CancellationException) {
                break
            } catch (e: Exception) {
                Log.e(TAG, "send error", e)
            }
        }
    }


    fun write(data: ByteArray) {
        scope.launch {
            sendChannel.send(data)
        }
    }


    /**
     * 发送数据（立即发送，不排队）
     */
     fun writeImmediately(dataStr: String) {
        synchronized(this) {
            try {
                val data = ByteUtil.hexStringToByteArray(dataStr)

                outputStream?.let { stream ->
                    stream.write(data)
                    stream.flush()

                    Log.d(TAG, "Data sent successfully: $dataStr")
                } ?: run {
                    Log.e(TAG, "OutputStream is null, cannot send data")
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error writing to OutputStream", e)
            }
        }
    }


    /**
     * 发送原始数据
     */
    fun sendRawDataWithOutHeadFoot(dataStr: String) {
        synchronized(this) {
            try {
                val data = ByteUtil.hexStringToByteArray(dataStr)

                outputStream?.let { stream ->
                    stream.write(data)
                    stream.flush()

                    Log.d(TAG, "sendRawDataWithOutHeadFoot Data sent successfully: $dataStr")
                } ?: run {
                    Log.e(TAG, "sendRawDataWithOutHeadFoot OutputStream is null, cannot send data")
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error writing to OutputStream", e)
            }
        }
    }


    fun writeImmediately(data: ByteArray) {
        try {
            outputStream?.write(data)
            outputStream?.flush()
        } catch (e: Exception) {
            Log.e(TAG, "write immediately error", e)
        }
    }


    private fun processReceivedData(buffer: ByteArray, len: Int) {
        if (len <= 0) return

        val hex = ByteUtil.bytesToHex(buffer.copyOf(len))

        Log.d(TAG, "receive=$hex")


        // 当前协议固定12字节
        if (len != 10) {
            return
        }

        val head = hex.substring(0, 4)
        val foot = hex.substring(16,20)

        if (head != HEAD || foot != FOOT) {
            return
        }


        val cmd = hex.substring(4, 6)
        val data = hex.substring(6, 14)
        Log.d(TAG, "cmd=$cmd data=$data")

        processCommand(cmd, data, hex)
    }


    private fun processCommand(
        cmd: String,
        data: String,
        fullData: String
    ) {

        try {
            when (cmd) {

                CMD_READ_T -> {
                    processTemperature(data)
                }

                CMD_LIGHT -> {
                    processLight(data)
                }

                A_STATE -> {
                    processAState(data, fullData)
                }

                B_STATE -> {
                    processBState(data, fullData)
                }

                HEATING_STATE -> {
                    processHeating(data)
                }

                A_VALUE -> {
                    processAValue(data)
                }

                B_VALUE -> {
                    processBValue(data)
                }

                SENSOR_LIGHT -> {
                    processSensorLight(data)
                }

                PUMP_MOTOR_VER -> {
                    processPumpVersion(data)
                }

                else -> {
                    Log.d(TAG, "unknown cmd=$cmd")
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "parse error cmd=$cmd", e)
        }
    }


    private fun processTemperature(data: String) {
        try {
            val integer = data.substring(0, 2).toInt(16)
            val decimal = data.substring(2, 4).toInt(16)
            val value = "$integer.$decimal"

            listeners.forEach {
                it.onTemperatureReceived(value)
            }

        } catch (e: Exception) {
            Log.e(TAG, "temperature error", e)
        }
    }


    private fun processLight(data: String) {
        try {
            val state = data.substring(1, 2) == "1"

            listeners.forEach {
                it.onLightStateReceived(state)
            }

        } catch (e: Exception) {
            Log.e(TAG, "light error", e)
        }
    }


    private fun processAState(
        data: String,
        fullData: String
    ) {

        try {
            val state = data.substring(1, 2).toInt()

            var duration = 0.0

            if (state == TestState.Finish) {
                duration = ComputeUtils.doubleFormat4(
                    (
                            fullData.substring(8, 16).toLong(16) +
                                    kotlin.random.Random.nextInt(10) * 0.1
                            ) * 0.001
                )
            }

            listeners.forEach {
                it.onADeviceState(state, duration)
            }

        } catch (e: Exception) {
            Log.e(TAG, "A state error", e)
        }
    }


    private fun processBState(
        data: String,
        fullData: String
    ) {

        try {
            val state = data.substring(1, 2).toInt()

            var duration = 0.0

            if (state == TestState.Finish) {
                duration = ComputeUtils.doubleFormat4(
                    (
                            fullData.substring(8, 16).toLong(16) +
                                    kotlin.random.Random.nextInt(10) * 0.1
                            ) * 0.001
                )
            }

            listeners.forEach {
                it.onBDeviceState(state, duration)
            }

        } catch (e: Exception) {
            Log.e(TAG, "B state error", e)
        }
    }


    private fun processHeating(data: String) {
        try {
            val state = data.substring(1, 2).toInt()

            listeners.forEach {
                it.onHeatingState(state)
            }

        } catch (e: Exception) {
            Log.e(TAG, "heating error", e)
        }
    }


    private fun processAValue(data: String) {
        try {
            val up = data.substring(0, 4).toInt(16)
            val down = data.substring(4, 8).toInt(16)

            listeners.forEach {
                it.onADetectedValue(up, down)
            }

        } catch (e: Exception) {
            Log.e(TAG, "A value error", e)
        }
    }


    private fun processBValue(data: String) {
        try {
            val up = data.substring(0, 4).toInt(16)
            val down = data.substring(4, 8).toInt(16)

            listeners.forEach {
                it.onBDetectedValue(up, down)
            }

        } catch (e: Exception) {
            Log.e(TAG, "B value error", e)
        }
    }


    private fun processSensorLight(data: String) {
        try {
            val aUp = data.substring(0, 2).toInt(16)
            val aDown = data.substring(2, 4).toInt(16)
            val bUp = data.substring(4, 6).toInt(16)
            val bDown = data.substring(6, 8).toInt(16)

            listeners.forEach {
                it.onSensorLightValue(
                    aUp,
                    aDown,
                    bUp,
                    bDown
                )
            }

        } catch (e: Exception) {
            Log.e(TAG, "sensor light error", e)
        }
    }


    private fun processPumpVersion(data: String) {
        try {
            val version = data.substring(0, 2).toInt(16)

            listeners.forEach {
                it.onPumpMotor(version)
            }

        } catch (e: Exception) {
            Log.e(TAG, "pump version error", e)
        }
    }

    /**********************
     * OTA
     **********************/

    private val otaStreamBuffer = ByteArrayOutputStream()

    @Volatile
    private var transferMode: TransferMode = TransferMode.NORMAL

    @Volatile
    private var otaCallback: OtaCallback? = null


    enum class TransferMode {
        NORMAL,
        OTA
    }


    private fun processOtaReceived(buffer: ByteArray, len: Int) {

        for (i in 0 until len) {

            when (buffer[i].toInt() and 0xFF) {

                0x43 -> { // 'C'
                    Log.d(TAG, "OTA: C received")
                    otaCallback?.onC()
                }

                0x06 -> { // ACK
                    Log.d(TAG, "OTA: ACK received")
                    otaCallback?.onAck()
                }

                0x15 -> { // NAK
                    Log.d(TAG, "OTA: NAK received")
                    otaCallback?.onNak()
                }

                0x18 -> { // CAN（可选）
                    Log.d(TAG, "OTA: CANCEL")
                    otaCallback?.onCancel()
                }
            }
        }
    }

    fun setOtaCallback(callback: OtaCallback) {
        otaCallback = callback
    }


    fun enterOtaMode() {
        Log.i(TAG, ">>> ENTER OTA MODE")

        transferMode = TransferMode.OTA

    }
    fun exitOtaMode() {
        Log.i(TAG, ">>> EXIT OTA MODE")

        transferMode = TransferMode.NORMAL

        otaCallback = null
    }

    fun writeBytes(bytes: ByteArray) {
        try {
            outputStream?.write(bytes)
            outputStream?.flush()
        } catch (e: Exception) {
            Log.e(TAG, "writeBytes error", e)
        }
    }


    /**********************
     * 串口关闭
     **********************/


    fun close() {

        Log.d(TAG, "close serial")

        running.set(false)

        try {
            sendChannel.close()

            outputStream?.close()
            inputStream?.close()

            serialPort?.close()

        } catch (e: Exception) {
            Log.e(TAG, "close error", e)
        }


        serialPort = null
        inputStream = null
        outputStream = null


        listeners.clear()
        otaCallback = null

        _connectionState.value =
            ConnectionState.DISCONNECTED
    }



    fun isOpen(): Boolean {
        return running.get() && serialPort != null
    }



    /**********************
     * 查询串口
     **********************/


    fun listSerialPorts(): List<String> {

        val result = mutableListOf<String>()

        try {

            File("/dev")
                .listFiles()
                ?.forEach {

                    val path = it.absolutePath

                    if (
                        path.startsWith("/dev/ttyS") ||
                        path.startsWith("/dev/ttyUSB") ||
                        path.startsWith("/dev/ttyACM")
                    ) {
                        result.add(path)
                    }
                }

        } catch (e: Exception) {
            Log.e(TAG, "list serial error", e)
        }

        return result
    }
}