package com.hm.viscosityauto.vm

import android.content.Context
import android.os.CountDownTimer
import android.serialport.SerialPort
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hm.viscosity.model.MediumModel
import com.hm.viscosityauto.GlobalState
import com.hm.viscosityauto.model.PointTModel
import com.hm.viscosityauto.MyApp
import com.hm.viscosityauto.R
import com.hm.viscosityauto.model.AdvParamModel
import com.hm.viscosityauto.model.DeviceParamModel
import com.hm.viscosityauto.model.DurationModel
import com.hm.viscosityauto.model.PassageModel
import com.hm.viscosityauto.room.AppDatabase
import com.hm.viscosityauto.room.admin.AdminRecords
import com.hm.viscosityauto.room.test.TestRecords
import com.hm.viscosityauto.utils.ByteUtil
import com.hm.viscosityauto.utils.ComputeUtils
import com.hm.viscosityauto.utils.ComputeUtils.divideAndFormat
import com.hm.viscosityauto.utils.ComputeUtils.moterSpeedConvert
import com.hm.viscosityauto.utils.CountTimer
import com.hm.viscosityauto.utils.LimitUtil
import com.hm.viscosityauto.utils.SPUtils
import com.hm.viscosityauto.utils.SerialPortManager
import com.hm.viscosityauto.utils.SerialPortManager.CRC
import com.hm.viscosityauto.utils.StringUtils
import com.hm.viscosityauto.utils.TimeUtils
import com.hm.viscosityauto.utils.TimeUtils.splitDateTime
import com.hm.viscosityauto.vm.CalibrationState.Mul
import com.hm.viscosityauto.vm.CalibrationState.None
import com.hm.viscosityauto.vm.CalibrationState.Single
import com.hm.viscosityauto.vm.HeatState.Empty
import com.hm.viscosityauto.vm.HeatState.Keeping
import com.hm.viscosityauto.vm.TestCMD.CMD_Clean
import com.hm.viscosityauto.vm.TestCMD.CMD_CleanEmpty
import com.hm.viscosityauto.vm.TestCMD.CMD_DecomP
import com.hm.viscosityauto.vm.TestCMD.CMD_Drying
import com.hm.viscosityauto.vm.TestCMD.CMD_Running
import com.hm.viscosityauto.vm.TestCMD.CMD_Stop
import com.hm.viscosityauto.vm.TestState.Clean
import com.hm.viscosityauto.vm.TestState.CleanEmpty
import com.hm.viscosityauto.vm.TestState.DecomP
import com.hm.viscosityauto.vm.TestState.Drying
import com.hm.viscosityauto.vm.TestState.Finish
import com.hm.viscosityauto.vm.TestState.FinishAll
import com.hm.viscosityauto.vm.TestState.Running
import com.hm.viscosityauto.vm.TestState.Start
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.text.DecimalFormat

object CalibrationState {
    const val None = 0
    const val Single = 1
    const val Mul = 2

}

//存储通道配置 key
const val CHANNEL_A = "channel_A"
const val CHANNEL_B = "channel_B"


const val PATH = "/dev/ttyS1"

object TestCMD {
    const val CMD_Stop = 0
    const val CMD_Running = 1
    const val CMD_Clean = 2//清洗
    const val CMD_CleanEmpty = 3//排空
    const val CMD_Drying = 4//烘干
    const val CMD_DecomP = 5//泄压
}

object TestState {
    const val Empty = 0
    const val Running = 1
    const val Start = 2
    const val Finish = 3
    const val Clean = 4//清洗
    const val CleanEmpty = 5//排空
    const val Drying = 6//烘干
    const val DecomP = 7//泄压
    const val FinishAll = 8
//    fun getTestCMD(state: Int): Int {
//        return when (state) {
//            Empty -> CMD_Stop
//            Running, Start, Finish -> CMD_Running
//            Clean -> CMD_Clean
//            CleanEmpty -> CMD_CleanEmpty
//            Drying -> CMD_Drying
//            DecomP -> CMD_DecomP
//            else -> CMD_Stop
//        }
//    }
}


object HeatState {
    const val Empty = 0
    const val Heating = 1
    const val Keeping = 2//恒温保持中
}


class TestVM : ViewModel() {
    val admin =
        Gson().fromJson(
            SPUtils.getInstance().getString("adminInfo", ""),
            AdminRecords::class.java
        )

    //设备串口通信
    private var serialPortManager: SerialPortManager? = null

    //通道计时器
    var timerA: CountTimer = CountTimer(intervalMillis = 1)
    var timerB: CountTimer = CountTimer(intervalMillis = 1)

    //恒温计时器
//    var timerKeepTA: CountDownTimer? = null
//    var timerKeepTB: CountDownTimer? = null

    private var timerJobA: Job? = null
    private var timerJobB: Job? = null


    //恒温剩余时间
    var keepTCountA by mutableStateOf("")
    var keepTCountB by mutableStateOf("")


    //温度
    var setTemperature: String by mutableStateOf(
        SPUtils.getInstance().getString("setTemperature", "40.00")
    )
    var curTemperature by mutableStateOf("--")
    var rawTemperature by mutableStateOf("--") //原始温度

    //加热状态
    var heatingState by mutableIntStateOf(Empty)

    //照明状态
    var lightState = mutableStateOf(GlobalState.lightState)

    //自动打印
    var autoPrint = mutableStateOf(SPUtils.getInstance().getBoolean("autoPrint", true))

    //自动上传
    var autoUpload = mutableStateOf(SPUtils.getInstance().getBoolean("autoUpload", true))

    //自动清洗
    var autoClean = mutableStateOf(SPUtils.getInstance().getBoolean("autoClean", true))

    //自动排空
    var autoEmpty = mutableStateOf(SPUtils.getInstance().getBoolean("autoEmpty", true))

    //数据优化
    var dataOpt = mutableStateOf(SPUtils.getInstance().getBoolean("dataOpt", false))


    //单点校准数值  设置值
    var offsetSetT: String by mutableStateOf(
        SPUtils.getInstance().getString("offsetSetT", "0.00")
    )

    var offsetRealT: String by mutableStateOf(
        SPUtils.getInstance().getString("offsetRealT", "0.00")
    )

    //整体系数数值
//    var mulSetT: String by mutableStateOf(
//        SPUtils.getInstance().getString("mulSetT", "1.00")
//    )
//
//    var mulRealT: String by mutableStateOf(
//        SPUtils.getInstance().getString("mulRealT", "1.00")
//    )


    //校准模式
    val calibrationState = mutableIntStateOf(None)

    //多点校准 系数  y=ax+b   x测试温度    y实际温度
    var pointA = 1f
    var pointB = 0f


    //多点校准数值
    var pointTStr: String by mutableStateOf(
        SPUtils.getInstance().getString("pointT", "")
    )
    var pointTList: MutableList<PointTModel> = mutableStateListOf()


    private var pointTStrDef: String by mutableStateOf(
        SPUtils.getInstance().getString("pointTDef", "")
    )

    //多点校准数值 默认出场值
    var pointTListDef: MutableList<PointTModel> = mutableStateListOf()

    //介质列表
    var mediumList = mutableStateListOf<MediumModel>()

    private val advParamModel: AdvParamModel by
    mutableStateOf(
        Gson().fromJson(
            SPUtils.getInstance().getString("advParamModel", Gson().toJson(AdvParamModel())),
            AdvParamModel::class.java
        )
    )


    //通道数据
    var passageModelA by mutableStateOf(PassageModel(id = 1))
    var passageModelB by mutableStateOf(PassageModel(id = 2))
    var ATimekeeping by mutableFloatStateOf(0.00f)
    var BTimekeeping by mutableFloatStateOf(0.00f)
    var showDataOptA by mutableStateOf(false)
    var showDataOptB by mutableStateOf(false)


    //初始化串口 用于打印的 后续可以将Act中的输出流对象换成这里的输出流对象
    private lateinit var mSerialPort: SerialPort //串口对象
    private var mOutputStream: OutputStream? = null //串口的输出流对象 用于发送指令

    //数据库
    private var DB: AppDatabase = AppDatabase.getDatabase(MyApp.getInstance())


    //设备参数
    var DeviceParamModel by mutableStateOf(DeviceParamModel())

    var configList: MutableList<PassageModel> = mutableStateListOf()

    /**
     * 初始化
     * -数据库
     * -打印串口
     * -通道计时器
     */
    init {
        Log.e("TestVM", "init")

        viewModelScope.launch {
            launch { initDevicePort() }
            delay(100)
            getLocalSetting()

            launch { initPrintPort() }

        }

        timerA.onTimeUpdate = {
            ATimekeeping = divideAndFormat(it.toFloat(), 1000)

        }

        timerB.onTimeUpdate = {
            BTimekeeping = divideAndFormat(it.toFloat(), 1000)
        }

    }

    override fun onCleared() {
        super.onCleared()
        Log.e("TestVM", "onCleared")

        stopTemperature()

        closeSerialPort()
        closeTimer()
        stopKeepTTimer(1)
        stopKeepTTimer(2)
    }

    /**
     * 加载本地 设置数据
     */
    fun getLocalSetting() {
        if (SPUtils.getInstance().getString(CHANNEL_A, "").isNotEmpty()) {
            passageModelA =
                Gson().fromJson(
                    SPUtils.getInstance().getString(CHANNEL_A, ""),
                    PassageModel::class.java
                )
        }
        if (SPUtils.getInstance().getString(CHANNEL_B, "").isNotEmpty()) {
            passageModelB =
                Gson().fromJson(
                    SPUtils.getInstance().getString(CHANNEL_B, ""),
                    PassageModel::class.java
                )
        }

        pointTStrDef = SPUtils.getInstance().getString("pointTDef", "")
        pointTListDef.clear()
        if (pointTStrDef.isNotEmpty()) {
            val listType = object : TypeToken<List<PointTModel>>() {}.type
            pointTListDef.addAll(Gson().fromJson(pointTStrDef, listType))
        } else {
            for (i in 0 until 5) {
                pointTListDef.add(PointTModel("0.00", "0.00"))
            }
        }

        pointTStr = SPUtils.getInstance().getString("pointT", "")
        pointTList.clear()
        if (pointTStr.isNotEmpty()) {
            val listType = object : TypeToken<List<PointTModel>>() {}.type
            pointTList.addAll(Gson().fromJson(pointTStr, listType))
        } else {
            for (i in 0 until 5) {
                pointTList.add(PointTModel("0.00", "0.00"))
            }
        }

        calibrationState.intValue =
            SPUtils.getInstance().getInt("calibrationState", None)
        if (calibrationState.intValue == Mul) {
            calculatePointFactor()
        }


        val mediumInfo = SPUtils.getInstance().getString("mediumInfo", "")
        if (mediumInfo.isEmpty()) {
            mediumList.add(MediumModel(p = "4", name = "硅油", isSel = true))
            SPUtils.getInstance().put("mediumInfo", Gson().toJson(mediumList))
        } else {
            val listType = object : TypeToken<List<MediumModel>>() {}.type
            mediumList.clear()
            mediumList.addAll(Gson().fromJson(mediumInfo, listType))
        }


        val configInfo = SPUtils.getInstance().getString("configInfo", "")
        if (configInfo.isEmpty()) {
            for (i in 0 until 9) {
                configList.add(PassageModel(testCount = "0"))
            }
            SPUtils.getInstance().put("configInfo", Gson().toJson(configList))
        } else {
            val listType = object : TypeToken<List<PassageModel>>() {}.type
            configList.clear()
            configList.addAll(Gson().fromJson(configInfo, listType))
        }

        val deviceParam = SPUtils.getInstance().getString("deviceParam", "")
        if (deviceParam.isNotEmpty()) {
            DeviceParamModel = Gson().fromJson(deviceParam, DeviceParamModel::class.java)
        }


        if (!GlobalState.isSetAdvParam) {
            viewModelScope.launch {
                setAdvParam(advParamModel)

                setMedium(mediumList.find {
                    it.isSel
                }?.p!!.toInt())

                GlobalState.isSetAdvParam = true
            }
        }


    }


    /**
     * 初始化 设备串口
     */
    private fun initDevicePort() {

        serialPortManager =
            SerialPortManager(PATH, 9600, object :
                SerialPortManager.OnDataReceivedListener {

                override fun onTemperatureReceived(temperature: String?) {
                    viewModelScope.launch {
                        rawTemperature = temperature.toString()
                        curTemperature = getTemperature(temperature.toString())
                    }
                }

                override fun onLightStateReceived(state: Boolean?) {
                    viewModelScope.launch {
                        if (state != null) {
                            lightState.value = state
                            GlobalState.lightState = state
                        }
                    }
                }

                override fun onHeatingState(state: Int) {
                    viewModelScope.launch {
                        if (heatingState != state) {
                            heatingState = state
                        }

                    }
                }

                override fun onADeviceState(state: Int) {
                    viewModelScope.launch {

                        if (passageModelA.state != state) {

                            when (state) {
                                Running -> {
                                    passageModelA = passageModelA.copy(state = Running)
                                }

                                Start -> {
                                    timerA.start()
                                    passageModelA = passageModelA.copy(
                                        state = Start,
                                        curNum = passageModelA.curNum + 1
                                    )
                                }

                                Finish -> {
                                    timerA.stop()
                                    passageModelA.durationArray.add(
                                        DurationModel(
                                            ComputeUtils.floatFormat(
                                                ATimekeeping
                                            )
                                        )
                                    )
                                    ATimekeeping = 0.00f

                                    if (passageModelA.curNum == passageModelA.testCount.toInt()) {
                                        if (!dataOpt.value) {
                                            passageModelA = passageModelA.copy(
                                                time = TimeUtils.timestampToString(),
                                                temperature = setTemperature
                                            )
                                            passageModelA.computeViscosity()
                                            saveDate(passageModelA)
                                        } else {
                                            showDataOptA = true
                                        }

                                        if (autoEmpty.value) {
                                            delay(2000)
                                            passageModelA = passageModelA.copy(state = CleanEmpty)
                                            setTestState(passageModelA.id, CMD_CleanEmpty)
                                        } else {//没有排空 直接完成
                                            passageModelA = passageModelA.copy(
                                                state = FinishAll,
                                            )

                                        }
                                    } else {
                                        delay(2000)
                                        passageModelA =
                                            passageModelA.copy(
                                                state = Running
                                            )
                                        setTestState(passageModelA.id, CMD_Running)
                                    }
                                }

                                CleanEmpty -> {
                                    passageModelA = passageModelA.copy(state = CleanEmpty)
                                }


                                TestState.Empty -> {
                                    when (passageModelA.state) {

                                        Clean -> {
                                            if (passageModelA.curCleanNum == passageModelA.cleanTimes.toInt()) {
                                                passageModelA = passageModelA.copy(
                                                    state = FinishAll,
                                                    time = TimeUtils.timestampToString(),
                                                    temperature = setTemperature
                                                )
                                            } else {
                                                passageModelA = passageModelA.copy(
                                                    state = Clean,
                                                    curCleanNum = passageModelA.curCleanNum + 1
                                                )
                                                delay(2000)
                                                setTestState(passageModelA.id, CMD_Clean)
                                            }
                                        }

                                        CleanEmpty -> {
                                            if (passageModelA.cleanTimes.toInt() > 0 && autoClean.value) {
                                                delay(2000)
                                                passageModelA = passageModelA.copy(
                                                    state = Clean,
                                                    curCleanNum = passageModelA.curCleanNum + 1
                                                )
                                                setTestState(passageModelA.id, CMD_Clean)
                                            } else {//没有清洗 直接完成
                                                passageModelA = passageModelA.copy(
                                                    state = FinishAll,
                                                )
                                            }

                                        }

                                    }

                                }
                            }

                        }

                    }

                }

                override fun onBDeviceState(state: Int) {
                    viewModelScope.launch {
                        if (passageModelB.state != state) {

                            when (state) {
                                Running -> {
                                    passageModelB = passageModelB.copy(state = Running)
                                }

                                Start -> {
                                    timerB.start()

                                    passageModelB = passageModelB.copy(
                                        state = Start,
                                        curNum = passageModelB.curNum + 1
                                    )

                                }

                                Finish -> {
                                    timerB.stop()
                                    passageModelB.durationArray.add(
                                        DurationModel(
                                            ComputeUtils.floatFormat(
                                                BTimekeeping
                                            )
                                        )
                                    )
                                    BTimekeeping = 0.00f

                                    if (passageModelB.curNum == passageModelB.testCount.toInt()) {
                                        if (!dataOpt.value) {
                                            passageModelB = passageModelB.copy(
                                                time = TimeUtils.timestampToString(),
                                                temperature = setTemperature
                                            )
                                            passageModelB.computeViscosity()
                                            saveDate(passageModelB)
                                        } else {
                                            showDataOptB = true
                                        }
                                        if (autoEmpty.value) {
                                            delay(2000)
                                            passageModelB = passageModelB.copy(state = CleanEmpty)

                                            setTestState(passageModelB.id, CMD_CleanEmpty)
                                        } else {//没有排空 直接完成
                                            passageModelB = passageModelB.copy(
                                                state = FinishAll,
                                            )
                                        }

                                    } else {
                                        delay(2000)
                                        passageModelB =
                                            passageModelB.copy(
                                                state = Running
                                            )
                                        setTestState(passageModelB.id, CMD_Running)
                                    }
                                }

                                CleanEmpty -> {
                                    passageModelB = passageModelB.copy(state = CleanEmpty)
                                }

                                Empty -> {
                                    when (passageModelB.state) {
                                        Clean -> {
                                            if (passageModelB.curCleanNum == passageModelB.cleanTimes.toInt()) {
                                                passageModelB = passageModelB.copy(
                                                    state = FinishAll,
                                                    time = TimeUtils.timestampToString(),
                                                    temperature = setTemperature
                                                )
                                            } else {
                                                delay(2000)
                                                passageModelB = passageModelB.copy(
                                                    state = Clean,
                                                    curCleanNum = passageModelB.curCleanNum + 1
                                                )
                                                setTestState(passageModelB.id, CMD_Clean)
                                            }
                                        }

                                        CleanEmpty -> {//排空后  清洗
                                            if (passageModelB.cleanTimes.toInt() > 0 && autoClean.value) {
                                                delay(2000)
                                                passageModelB = passageModelB.copy(
                                                    state = Clean,
                                                    curCleanNum = passageModelB.curCleanNum + 1
                                                )
                                                setTestState(passageModelB.id, CMD_Clean)
                                            } else {//没有清洗 直接完成
                                                passageModelB = passageModelB.copy(
                                                    state = FinishAll,
                                                    time = TimeUtils.timestampToString(),
                                                    temperature = setTemperature
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onADetectedValue(valueUp: Int, valueDown: Int) {
                    DeviceParamModel = DeviceParamModel.copy(aUp = valueUp, aDown = valueDown)
                }

                override fun onBDetectedValue(valueUp: Int, valueDown: Int) {
                    DeviceParamModel = DeviceParamModel.copy(bUp = valueUp, bDown = valueDown)

                }


            })


    }

    /**
     * 初始化 打印串口
     */
    private fun initPrintPort() {

        try {
            val device = File("/dev/ttyS7")
            mSerialPort = SerialPort // 串口对象
                .newBuilder(device, 9600) // 串口地址地址，波特率
                .dataBits(8) // 数据位,默认8；可选值为5~8
                .stopBits(1) // 停止位，默认1；1:1位停止位；2:2位停止位
                .parity(0) // 校验位；0:无校验位(NONE，默认)；1:奇校验位(ODD);2:偶校验位(EVEN)
                .build() // 打开串口并返回
            mOutputStream = mSerialPort.outputStream

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    /**
     * 开始测试
     * channel  1 :A  2:B
     */
    fun startPassage(context: Context, channel: Int) {
        if (channel == 1) {
            passageModelA = passageModelA.copy(
                state = TestState.Empty,
                curNum = 0, duration = "0.00",
                curCleanNum = 0, viscosity = "0.000",
                durationArray = ArrayList()
            )

        } else {
            passageModelB = passageModelB.copy(
                state = TestState.Empty,
                curNum = 0, duration = "0.00",
                curCleanNum = 0, viscosity = "0.000",
                durationArray = ArrayList()
            )
        }

//        if (!lightState.value) {
//            setLightState(true)
//        }

        viewModelScope.launch {
            if (heatingState == Empty) {
                if (LimitUtil.isOverLimit(MyApp.getInstance(), setTemperature)) {
                    return@launch
                }
                if (passageModelA.state != Empty || passageModelB.state != Empty) {
                    Toast.makeText(
                        MyApp.getInstance(),
                        MyApp.getInstance().getString(R.string.edit_t_tip),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                setTemperature(setTemperature)

                setParam()
                if (channel == 1) {
                    passageModelA = passageModelA.copy(state = Running)
                    keepTCountA = context.getString(R.string.controllingT)
                } else {
                    passageModelB = passageModelB.copy(state = Running)
                    keepTCountB = context.getString(R.string.controllingT)

                }

            } else if (heatingState == Keeping) {
                setParam()
                if (channel == 1) {
                    passageModelA = passageModelA.copy(state = Running)
                    keepTCountA = context.getString(R.string.controllingT)
                } else {
                    passageModelB = passageModelB.copy(state = Running)
                    keepTCountB = context.getString(R.string.controllingT)

                }

                startKeepTTimer(
                    context,
                    channel,
                    if (channel == 1) passageModelA.keepTDuration.toInt() else passageModelB.keepTDuration.toInt()
                )
            } else {
                setParam()
                if (channel == 1) {
                    passageModelA = passageModelA.copy(state = Running)
                    keepTCountA = context.getString(R.string.controllingT)
                } else {
                    passageModelB = passageModelB.copy(state = Running)
                    keepTCountB = context.getString(R.string.controllingT)

                }
            }

        }

    }


    /**
     * 结束测试
     * channel  1 :A  2:B
     */
    fun endPassage(channel: Int) {
        if (channel == 1) {
            passageModelA = passageModelA.copy(
                state = TestState.Empty,
                curCleanNum = 0,
                curNum = 0,
                durationArray = ArrayList()
            )
            timerA.stop()
            ATimekeeping = 0.00f

        } else {
            passageModelB = passageModelB.copy(
                state = TestState.Empty,
                curCleanNum = 0,
                curNum = 0,
                durationArray = ArrayList()
            )
            timerB.stop()
            BTimekeeping = 0.00f
        }
        setTestState(channel, CMD_Stop)
    }


    /**
     * 设置测试状态
     */
    fun setTestState(channel: Int, state: Int) {
        val byteArray: ByteArray = ByteUtil.hexStringToByteArray(
            SerialPortManager.HEAD + (if (channel == 1) SerialPortManager.A_CMD else SerialPortManager.B_CMD) + ByteUtil.intToHex(
                state
            ) + "000000" + CRC + SerialPortManager.FOOT
        )
        ByteUtil.printByteArray(byteArray)
        serialPortManager?.write(byteArray)
    }


    private suspend fun setParam() {
        //设置参数
        //清洗时间
        val cleanDurationA =
            (if (passageModelA.cleanDuration.toIntOrNull() == null) 0 else passageModelA.cleanDuration.toInt())
        val cleanDurationB =
            (if (passageModelB.cleanDuration.toIntOrNull() == null) 0 else passageModelB.cleanDuration.toInt())

        var byteArray = ByteUtil.hexStringToByteArray(
            SerialPortManager.HEAD + SerialPortManager.CMD_SET_CLEAN_DURATION + ByteUtil.intToHex4(
                cleanDurationA
            ) + ByteUtil.intToHex4(cleanDurationB) + CRC + SerialPortManager.FOOT
        )
        ByteUtil.printByteArray(byteArray)
        serialPortManager?.write(byteArray)

        delay(50)

        //进清洗液时间
        val addDurationA =
            (if (passageModelA.addDuration.toIntOrNull() == null) 0 else passageModelA.addDuration.toInt())
        val addDurationB =
            (if (passageModelB.addDuration.toIntOrNull() == null) 0 else passageModelB.addDuration.toInt())

        byteArray = ByteUtil.hexStringToByteArray(
            SerialPortManager.HEAD + SerialPortManager.CMD_LIQUID_ENTER_DURATION + ByteUtil.intToHex4(
                addDurationA
            ) + ByteUtil.intToHex4(addDurationB) + CRC + SerialPortManager.FOOT
        )
        ByteUtil.printByteArray(byteArray)
        serialPortManager?.write(byteArray)
        delay(50)

        //进清抽提时间
        val extractDurationA =
            (if (passageModelA.extractDuration.toIntOrNull() == null) 0 else passageModelA.extractDuration.toInt())
        val extractDurationB =
            (if (passageModelB.extractDuration.toIntOrNull() == null) 0 else passageModelB.extractDuration.toInt())

        byteArray = ByteUtil.hexStringToByteArray(
            SerialPortManager.HEAD + SerialPortManager.CMD_EXTRACT_DURATION + ByteUtil.intToHex4(
                extractDurationA
            ) + ByteUtil.intToHex4(extractDurationB) + CRC + SerialPortManager.FOOT
        )
        ByteUtil.printByteArray(byteArray)
        serialPortManager?.write(byteArray)

        delay(50)

        //进清抽提间隔
        val extractIntervalA =
            (if (passageModelA.extractInterval.toIntOrNull() == null) 0 else passageModelA.extractInterval.toInt())
        val extractIntervalB =
            (if (passageModelB.extractInterval.toIntOrNull() == null) 0 else passageModelB.extractInterval.toInt())

        byteArray = ByteUtil.hexStringToByteArray(
            SerialPortManager.HEAD + SerialPortManager.CMD_EXTRACT_INTERVAL + ByteUtil.intToHex4(
                extractIntervalA
            ) + ByteUtil.intToHex4(extractIntervalB) + CRC + SerialPortManager.FOOT
        )
        ByteUtil.printByteArray(byteArray)
        serialPortManager?.write(byteArray)
        delay(50)

        //电机速度
        val motorSpeedA =
            (if (passageModelA.motorSpeed.toIntOrNull() == null) 0 else passageModelA.motorSpeed.toInt())
        val motorSpeedB =
            (if (passageModelB.motorSpeed.toIntOrNull() == null) 0 else passageModelB.motorSpeed.toInt())

        byteArray = ByteUtil.hexStringToByteArray(
            SerialPortManager.HEAD + SerialPortManager.MOTOR_SPEED + ByteUtil.intToHex(
                motorSpeedA
            ) + "00" + ByteUtil.intToHex(moterSpeedConvert(motorSpeedB)) + "00" + CRC + SerialPortManager.FOOT
        )
        ByteUtil.printByteArray(byteArray)
        serialPortManager?.write(byteArray)

    }


    /**
     * 设置 高级参数
     */
    suspend fun setAdvParam(advParamModel: AdvParamModel) {
        //排空电机速度
        var byteArray = ByteUtil.hexStringToByteArray(
            SerialPortManager.HEAD + SerialPortManager.EMPTY_MOTOR_SPEED + ByteUtil.intToHex(
                advParamModel.emptySpeed.toInt()
            ) + "000000" + CRC + SerialPortManager.FOOT
        )
        ByteUtil.printByteArray(byteArray)
        serialPortManager?.write(byteArray)
        delay(50)
        //排空抽提时间
        byteArray = ByteUtil.hexStringToByteArray(
            SerialPortManager.HEAD + SerialPortManager.EMPTY_EXTRACT_DURATION + ByteUtil.intToHex4(
                advParamModel.emptyExtractDuration.toInt()
            ) + "0000" + CRC + SerialPortManager.FOOT
        )
        ByteUtil.printByteArray(byteArray)
        serialPortManager?.write(byteArray)
        delay(50)
        //排空抽提间隔
        byteArray = ByteUtil.hexStringToByteArray(
            SerialPortManager.HEAD + SerialPortManager.EMPTY_EXTRACT_INTERVAL + ByteUtil.intToHex4(
                advParamModel.emptyExtractInterval.toInt()
            ) + "0000" + CRC + SerialPortManager.FOOT
        )
        ByteUtil.printByteArray(byteArray)
        serialPortManager?.write(byteArray)
        delay(50)
        //排空烘干时间
        byteArray = ByteUtil.hexStringToByteArray(
            SerialPortManager.HEAD + SerialPortManager.EMPTY_DRYING_DURATION + ByteUtil.intToHex4(
                advParamModel.emptyDryingDuration.toInt()
            ) + "0000" + CRC + SerialPortManager.FOOT
        )
        ByteUtil.printByteArray(byteArray)
        serialPortManager?.write(byteArray)
        delay(50)
        //清洗电机速度
        byteArray = ByteUtil.hexStringToByteArray(
            SerialPortManager.HEAD + SerialPortManager.CLEAN_MOTOR_SPEED + ByteUtil.intToHex(
                advParamModel.cleanSpeed.toInt()
            ) + "000000" + CRC + SerialPortManager.FOOT
        )
        ByteUtil.printByteArray(byteArray)
        serialPortManager?.write(byteArray)
        delay(50)
        //清洗烘干时间
        byteArray = ByteUtil.hexStringToByteArray(
            SerialPortManager.HEAD + SerialPortManager.CLEAN_DRYING_DURATION + ByteUtil.intToHex4(
                advParamModel.cleanDryingDuration.toInt()
            ) + "0000" + CRC + SerialPortManager.FOOT
        )
        ByteUtil.printByteArray(byteArray)
        serialPortManager?.write(byteArray)


    }


    /**
     * 设置 灯 开关
     */
    fun setLightState(state: Boolean) {

        val byteArray = ByteUtil.hexStringToByteArray(
            SerialPortManager.HEAD + SerialPortManager.CMD_LIGHT + (if (state) "01" else "00") + "000000" + CRC + SerialPortManager.FOOT
        )
        ByteUtil.printByteArray(byteArray)
        serialPortManager?.write(byteArray)

    }


    /**
     * 计算 温度
     * temperature 从设备读取的温度
     */
    private fun getTemperature(temperature: String): String {
        var t = "0"
        val df = DecimalFormat("#.00")

        when (calibrationState.intValue) {
            0 -> {
                t = temperature
            }

            1 -> {
                val diff = offsetRealT.toFloat() - offsetSetT.toFloat()
                Log.e("getTemperature", (temperature.toFloat() + diff).toString())


                t = df.format((temperature.toFloat() + diff))
            }

            2 -> {
                t = df.format((pointA * temperature.toFloat() + pointB))
            }
        }


        return t
    }


    /**
     * 计算 多点校准系数
     */
    private fun calculatePointFactor() {
        val tempList: MutableList<PointTModel> = mutableStateListOf()
        tempList.addAll(pointTList)
        tempList.removeIf {
            it == PointTModel("0.00", "0.00")
        }
        tempList.sortBy {
            it.testT.toFloat()
        }

        val size = tempList.size

        Log.e("tempList size", size.toString())


        if (size < 2) {
            pointA = 1f
            pointB = 0f
        } else if (size == 2) {

            pointA = divideAndFormat(
                tempList[1].realT.toFloat() - tempList[0].realT.toFloat(),
                tempList[1].testT.toFloat() - tempList[0].testT.toFloat()
            )
            pointB = tempList[1].realT.toFloat() - pointA * tempList[1].testT.toFloat()
        } else {
            var position = 0

            tempList.forEachIndexed { index, pointTModel ->
                if (setTemperature.toFloat() > pointTModel.testT.toFloat()) {
                    position = index
                }
            }

            when (position) {
                0 -> {//用开始两条计算
                    Log.e("calculatePointFactor", "用开始两条计算")


                    pointA = divideAndFormat(
                        tempList[1].realT.toFloat() - tempList[0].realT.toFloat(),
                        tempList[1].testT.toFloat() - tempList[0].testT.toFloat()
                    )
                    pointB = tempList[1].realT.toFloat() - pointA * tempList[1].testT.toFloat()
                }

                size - 1 -> {//用最后两条计算

                    Log.e("calculatePointFactor", "用最后两条计算")
                    pointA = divideAndFormat(
                        tempList[size - 1].realT.toFloat() - tempList[size - 2].realT.toFloat(),
                        tempList[size - 1].testT.toFloat() - tempList[size - 2].testT.toFloat()
                    )
                    pointB =
                        tempList[size - 1].realT.toFloat() - pointA * tempList[size - 1].testT.toFloat()
                }

                else -> {//用相邻两条计算
                    pointA = divideAndFormat(
                        tempList[position + 1].realT.toFloat() - tempList[position].realT.toFloat(),
                        tempList[position + 1].testT.toFloat() - tempList[position].testT.toFloat()
                    )
                    pointB =
                        tempList[position + 1].realT.toFloat() - pointA * tempList[position + 1].testT.toFloat()
                    Log.e(
                        "calculatePointFactor",
                        "其他${pointA * tempList[position + 1].testT.toFloat()}"
                    )

                }
            }

            Log.e("calculatePointFactor", "$pointA   $pointB")

        }
    }


    /**
     * 设置 温度
     */
    fun setTemperature(setTemperature: String) {
        if (LimitUtil.isOverLimit(MyApp.getInstance(), setTemperature)) {
            return
        }
        if (passageModelA.state != Empty || passageModelB.state != Empty) {
            Toast.makeText(
                MyApp.getInstance(),
                MyApp.getInstance().getString(R.string.edit_t_tip),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        SPUtils.getInstance().put("setTemperature", setTemperature)
        this.setTemperature = setTemperature
        val beforeDecimal: String
        val afterDecimal: String
        val byteArray: ByteArray

        var writeTemperature = setTemperature
        //处理温度校准计算
        when (calibrationState.intValue) {
            None -> {
                writeTemperature = setTemperature
            }

            Single -> {
                val diff = offsetRealT.toFloat() - offsetSetT.toFloat()
                writeTemperature = (setTemperature.toFloat() - diff).toString()
            }

            Mul -> {
                calculatePointFactor()
                writeTemperature =
                    divideAndFormat(setTemperature.toFloat() - pointB, pointA).toString()
            }
        }
        val df = DecimalFormat("0.00")

        writeTemperature = df.format(writeTemperature.toFloat())

        Log.e("setTemperature", writeTemperature)
        if (writeTemperature.contains(".")) {
            // 取小数点前的数字
            beforeDecimal = writeTemperature.substring(0, writeTemperature.indexOf('.'))
            // 取小数点后的数字
            afterDecimal = writeTemperature.substring(writeTemperature.indexOf('.') + 1)
            byteArray = ByteUtil.hexStringToByteArray(
                SerialPortManager.HEAD + SerialPortManager.CMD_SET_T + ByteUtil.intToHex(
                    beforeDecimal.toInt()
                ) + ByteUtil.intToHex(afterDecimal.toInt()) + "010000" + SerialPortManager.FOOT
            )
        } else {
            beforeDecimal = writeTemperature
            byteArray = ByteUtil.hexStringToByteArray(
                SerialPortManager.HEAD + SerialPortManager.CMD_SET_T + ByteUtil.intToHex(
                    beforeDecimal.toInt()
                ) + "00" + "010000" + SerialPortManager.FOOT
            )
        }

        ByteUtil.printByteArray(byteArray)
        serialPortManager?.write(byteArray)

    }


    /**
     * 停止  控制温度
     */
    fun stopTemperature() {
        if (passageModelA.state != Empty || passageModelB.state != Empty) {
            Toast.makeText(
                MyApp.getInstance(),
                MyApp.getInstance().getString(R.string.edit_t_tip),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val byteArray: ByteArray = ByteUtil.hexStringToByteArray(
            SerialPortManager.HEAD + SerialPortManager.CMD_SET_T + "00" + "00" + "000000" + SerialPortManager.FOOT
        )
        ByteUtil.printByteArray(byteArray)
        serialPortManager?.write(byteArray)
    }


    /**
     * 设置介质
     * model  4硅油
     */
    fun setMedium(model: Int) {
        val byteArrayP: ByteArray = ByteUtil.hexStringToByteArray(
            SerialPortManager.HEAD + SerialPortManager.MEDIUM_VALUE + ByteUtil.intToHex(
                model
            ) + "00" + "000000" + SerialPortManager.FOOT
        )
        ByteUtil.printByteArray(byteArrayP)
        serialPortManager?.write(byteArrayP)

        SPUtils.getInstance().put("mediumInfo", Gson().toJson(mediumList))
    }


    /**
     * 打印实验结果
     */
    fun printData(context: Context, model: PassageModel) {
        printData(
            context,
            model.number,
            model.time,
            model.duration,
            model.viscosity,
            model.constant,
            model.temperature,
            Gson().toJson(model.durationArray),
            admin.name
        )
    }

    /**
     * 打印实验结果
     */
    fun printData(
        context: Context,
        number: String,
        time: String,
        duration: String,
        viscosity: String,
        constant: String,
        temp: String,
        durationArray: String,
        tester: String,
    ) {

        val list: MutableList<ByteArray> = java.util.ArrayList()
        list.add(
            StringUtils.str2Bytes(
                "***********" + context
                    .getString(R.string.test_result) + "************" + "\r\n"
            )
        )

        list.add(
            StringUtils.str2Bytes(
                context
                    .getString(R.string.time) + ":    " + time + "\r\n" + "\r\n"
            )
        )
        list.add(
            StringUtils.str2Bytes(
                context
                    .getString(R.string.tester) + ":    " + tester + "\r\n" + "\r\n"
            )
        )

        list.add(
            StringUtils.str2Bytes(
                context.getString(R.string.number) + ":    " + number + "\r\n"
            )
        )
        list.add(
            StringUtils.str2Bytes(
                context
                    .getString(R.string.duration) + ":    " + duration + " s\r\n"
            )
        )


        if (durationArray.isNotEmpty()) {
            val lists = Gson().fromJson(
                durationArray,
                Array<DurationModel>::class.java
            ).toList()

            lists.forEachIndexed { index, it ->
                if (!it.derelict) {
                    list.add(
                        StringUtils.str2Bytes(
                            "    " +
                                    context.getString(R.string.number_start) + (index + 1) + (if (SPUtils.getInstance()
                                    .getString("language", LANGUAGE_ZH) == LANGUAGE_ZH
                            ) context.getString(
                                R.string.number_end
                            ) else " ") + "%.2f".format(it.duration) + " s\r\n"
                        )
                    )

                }
            }
        }


        list.add(
            StringUtils.str2Bytes(
                context
                    .getString(R.string.temperature) + ":    " + temp + " ℃\r\n"
            )
        )

        list.add(
            StringUtils.str2Bytes(
                context
                    .getString(R.string.viscosity_constant) + ":    " + constant + " mm2/s2\r\n" + "\r\n"
            )
        )

        list.add(
            StringUtils.str2Bytes(
                context
                    .getString(R.string.viscosity) + ":    " + viscosity + " mm2/s\r\n"
            )
        )

        list.add(("*******************************" + "\r\n").toByteArray(charset("GB2312")))
        list.add((" " + "\r\n").toByteArray(charset("GB2312")))
        list.add((" " + "\r\n").toByteArray(charset("GB2312")))
        list.add((" " + "\r\n").toByteArray(charset("GB2312")))

        viewModelScope.launch {

            withContext(Dispatchers.IO) {

                try {
                    list.forEach() {
                        mOutputStream?.write(it)
                    }

                } catch (e: IOException) {
                    Log.e("print", e.message.toString())
                    e.printStackTrace()
                }


            }
        }


    }


    fun saveDate(model: PassageModel) {
        val record = TestRecords(
            testNum = model.number,
            duration = model.duration,
            temperature = model.temperature,
            constant = model.constant,
            viscosity = model.viscosity,
            date = splitDateTime(model.time).first,
            time = splitDateTime(model.time).second,
            durationArray = Gson().toJson(model.durationArray),
            tester = admin.name
        )
        viewModelScope.launch(Dispatchers.IO) {
            DB.testDao().insert(record)
        }

        if (autoPrint.value) {
            printData(MyApp.getInstance(), model)
        }

    }


    fun closeSerialPort() {
        serialPortManager?.close()
        serialPortManager = null // 重要！解除引用
    }

    fun closeTimer() {
        if (timerA.isTimerRun()) {
            timerA.stop()
        }
        if (timerB.isTimerRun()) {
            timerB.stop()
        }

        stopKeepTTimer(1)
        stopKeepTTimer(2)
//        timerKeepTA?.cancel()
//        timerKeepTB?.cancel()

    }


    fun startKeepTTimer(
        context: Context,
        channel: Int,
        durationMillis: Int,
        intervalMillis: Long = 1000L
    ) {
        // 安全停止现有计时器
        stopKeepTTimer(channel)

        if (channel == 1) {

            timerJobA?.cancel() // 取消旧任务（线程安全）

            timerJobA = viewModelScope.launch {
                val endTime = System.currentTimeMillis() + durationMillis * 1000+100
                while (System.currentTimeMillis() < endTime) {
                    val remaining = endTime - System.currentTimeMillis()
                    keepTCountA = (remaining / 1000).toString()
                    delay(intervalMillis) // 精确控制间隔
                }
                keepTCountA = context.getString(R.string.keepedT)
                setTestState(channel, CMD_Running)
            }

//
//            timerKeepTA = object : CountDownTimer(durationMillis * 1000L, intervalMillis) {
//                override fun onTick(millisUntilFinished: Long) {
//                    // 更新UI
//                    keepTCountA = (millisUntilFinished / 1000).toString()
//
//                }
//
//                override fun onFinish() {
//                    // 完成操作
//                    keepTCountA = context.getString(R.string.keepedT)
//                    timerKeepTA = null  // 清除引用
//                    setTestState(channel, CMD_Running)
//                }
//            }.start()
        } else {
            timerJobB?.cancel() // 取消旧任务（线程安全）

            timerJobB = viewModelScope.launch {
                val endTime = System.currentTimeMillis() + durationMillis * 1000+100
                while (System.currentTimeMillis() < endTime) {
                    val remaining = endTime - System.currentTimeMillis()
                    keepTCountB = (remaining / 1000).toString()
                    delay(intervalMillis) // 精确控制间隔
                }
                keepTCountB = context.getString(R.string.keepedT)
                setTestState(channel, CMD_Running)
            }


//            timerKeepTB = object : CountDownTimer(durationMillis * 1000L, intervalMillis) {
//                override fun onTick(millisUntilFinished: Long) {
//                    // 更新UI
//                    keepTCountB = (millisUntilFinished / 1000).toString()
//
//                }
//
//                override fun onFinish() {
//                    // 完成操作
//                    keepTCountB = context.getString(R.string.keepedT)
//                    timerKeepTB = null  // 清除引用
//                    setTestState(channel, CMD_Running)
//                }
//            }.start()
        }


    }

    fun stopKeepTTimer(channel: Int) {
        if (channel == 1) {
            timerJobA?.cancel()
//            timerKeepTA?.cancel()   // 停止计时器
//            timerKeepTA = null  // 清除引用
        } else {
            timerJobB?.cancel()
//            timerKeepTB?.cancel()          // 停止计时器
//            timerKeepTB = null  // 清除引用
        }


    }

    fun setAutoUpload(state: Boolean) {
        this.autoUpload.value = state
        SPUtils.getInstance().put("autoUpload", state)
    }

    fun setAutoClean(state: Boolean) {
        if (state) {
            setAutoEmpty(true)
            this.autoClean.value = true
            SPUtils.getInstance().put("autoClean", true)
        } else {
            this.autoClean.value = false
            SPUtils.getInstance().put("autoClean", false)
        }

    }

    fun setAutoEmpty(state: Boolean) {
        if (state) {
            this.autoEmpty.value = true
            SPUtils.getInstance().put("autoEmpty", true)
        } else {
            this.autoEmpty.value = false
            SPUtils.getInstance().put("autoEmpty", false)
            setAutoClean(false)
        }
    }


    fun setAutoPrint(state: Boolean) {
        this.autoPrint.value = state
        SPUtils.getInstance().put("autoPrint", state)
    }

    fun setDataOpt(state: Boolean) {
        this.dataOpt.value = state
        SPUtils.getInstance().put("dataOpt", state)
    }

}



