package com.hm.viscosityauto.vm

import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hm.viscosityauto.model.MediumModel
import com.hm.viscosityauto.GlobalState
import com.hm.viscosityauto.model.PointTModel
import com.hm.viscosityauto.MyApp
import com.hm.viscosityauto.R
import com.hm.viscosityauto.model.AdvParamModel
import com.hm.viscosityauto.model.DeviceParamModel
import com.hm.viscosityauto.model.ExtractModel
import com.hm.viscosityauto.ui.view.ErrorView
import com.hm.viscosityauto.utils.ByteUtil
import com.hm.viscosityauto.utils.ComputeUtils.divideAndFormat
import com.hm.viscosityauto.utils.ComputeUtils.moterSpeedConvert
import com.hm.viscosityauto.utils.LimitUtil
import com.hm.viscosityauto.utils.PATH
import com.hm.viscosityauto.utils.SPUtils
import com.hm.viscosityauto.utils.SerialManager
import com.hm.viscosityauto.utils.SerialManager.Companion.CRC
import com.hm.viscosityauto.utils.ota.OtaController
import com.hm.viscosityauto.utils.ota.OtaStatus
import com.hm.viscosityauto.vm.CalibrationState.Mul
import com.hm.viscosityauto.vm.CalibrationState.None
import com.hm.viscosityauto.vm.CalibrationState.Single
import com.hm.viscosityauto.vm.HeatState.Empty
import com.hm.viscosityauto.vm.TestCMD.CMD_Stop
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.DecimalFormat


class SettingVM : ViewModel() {

    //设备串口通信
    private val serialManager = SerialManager.getInstance(PATH, 9600)

    //温度
    var setTemperature: String by mutableStateOf(
        SPUtils.getInstance().getString("setTemperature", "40.00")
    )
    var curTemperature by mutableStateOf("--")
    var rawTemperature by mutableStateOf("--") //原始温度

    //加热状态
    var heatingState by mutableIntStateOf(Empty)

    //照明状态
    var lightState = mutableStateOf(false)


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


    var stateA: Int by mutableIntStateOf(
        Empty
    )

    var stateB: Int by mutableIntStateOf(
        Empty
    )


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


    //设备参数 抽提信息
    var ExtractModelA by mutableStateOf(ExtractModel())
    var ExtractModelB by mutableStateOf(ExtractModel())

    //设备参数
    var DeviceParamModel by mutableStateOf(DeviceParamModel())

    var deviceParamModelConfigList: MutableList<DeviceParamModel> = mutableStateListOf()

    //泄压计时器
    var timerDecomP: CountDownTimer? = null

    //烘干计时器
    var timerDrying: CountDownTimer? = null

    var advParamModel by
    mutableStateOf(
        Gson().fromJson(
            SPUtils.getInstance().getString("advParamModel", Gson().toJson(AdvParamModel())),
            AdvParamModel::class.java
        )
    )

    ///泵机 版本型号
    var pumpMotro = mutableIntStateOf(0)

    ///固件 版本型号
    var firmVersion = mutableStateOf("1.0.0")

    private val listener = object : SerialManager.OnDataReceivedListener {

        override fun onTemperatureReceived(temperature: String) {
            viewModelScope.launch {
                rawTemperature = temperature
                curTemperature = getTemperature(temperature)
            }
        }

        override fun onLightStateReceived(state: Boolean) {
            viewModelScope.launch {
                lightState.value = state
            }
        }

        override fun onHeatingState(state: Int) {
            viewModelScope.launch {
                if (heatingState != state) {
                    heatingState = state
                }

            }
        }

        override fun onADeviceState(state: Int, dur: Double) {
            if (stateA != state) {


                stateA = state
            }

        }

        override fun onBDeviceState(state: Int, dur: Double) {
            if (stateB != state) {
                stateB = state
            }
        }

        override fun onADetectedValue(valueUp: Int, valueDown: Int) {
            DeviceParamModel =
                DeviceParamModel.copy(aUp = valueUp.toString(), aDown = valueDown.toString())
        }

        override fun onBDetectedValue(valueUp: Int, valueDown: Int) {
            DeviceParamModel =
                DeviceParamModel.copy(bUp = valueUp.toString(), bDown = valueDown.toString())

        }

        override fun onSensorLightValue(
            AvalueUp: Int,
            AvalueDown: Int,
            BvalueUp: Int,
            BvalueDown: Int
        ) {
            DeviceParamModel = DeviceParamModel.copy(
                aUpSensitivity = AvalueUp.toString(),
                aDownSensitivity = AvalueDown.toString(),
                bUpSensitivity = BvalueUp.toString(),
                bDownSensitivity = BvalueDown.toString()
            )

        }

        override fun onPumpMotor(version: Int) {
            pumpMotro.intValue = version
        }

        override fun onFirmVersion(version: String, code: Int) {
            firmVersion.value = version
        }

        override fun onError(code: Int) {
        }

    }


    /**
     * 初始化
     * -数据库
     * -打印串口
     * -通道计时器
     */
    init {
        Log.e("SettingVM", "init" + this)
        getLocalSetting()

        addListener()
    }

    override fun onCleared() {
        super.onCleared()
        Log.e("SettingVM", "onCleared" + this)
        stopTemperature()
    }

    /**
     * 加载本地 设置数据
     */
    fun getLocalSetting() {

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
            mediumList.add(MediumModel(p = "15", name = "硅油", isSel = true))
            mediumList.add(MediumModel(p = "8", name = "水", isSel = false))

            SPUtils.getInstance().put("mediumInfo", Gson().toJson(mediumList))
        } else {
            val listType = object : TypeToken<List<MediumModel>>() {}.type
            mediumList.clear()
            mediumList.addAll(Gson().fromJson(mediumInfo, listType))
        }

        val configInfo = SPUtils.getInstance().getString("deviceParamConfigInfo", "")
        if (configInfo.isEmpty()) {
            for (i in 0 until 10) {
                deviceParamModelConfigList.add(DeviceParamModel())
            }
            SPUtils.getInstance()
                .put("deviceParamConfigInfo", Gson().toJson(deviceParamModelConfigList))
        } else {
            val listType = object : TypeToken<List<DeviceParamModel>>() {}.type
            deviceParamModelConfigList.clear()
            deviceParamModelConfigList.addAll(Gson().fromJson(configInfo, listType))
        }

        val deviceParam = SPUtils.getInstance().getString("deviceParamInfo", "")
        if (deviceParam.isNotEmpty()) {
            DeviceParamModel = Gson().fromJson(deviceParam, DeviceParamModel::class.java)
        }

        val extractModelA = SPUtils.getInstance().getString("extractModelA", "")
        if (extractModelA.isNotEmpty()) {
            ExtractModelA = Gson().fromJson(extractModelA, ExtractModel::class.java)
        }
        val extractModelB = SPUtils.getInstance().getString("extractModelB", "")
        if (extractModelB.isNotEmpty()) {
            ExtractModelB = Gson().fromJson(extractModelB, ExtractModel::class.java)
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


    fun addListener() {
        serialManager.addListener(listener)
    }

    /**
     * 开启关闭  AB 检测值上报
     */
    fun startABValueUp(state: Boolean) {

        val byteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.AB_VALUE_UP + (if (state) "01" else "00") + "000000" + CRC + SerialManager.FOOT
        )
        serialManager.write(byteArray)

    }

    /**
     * 查询	四个传感器光强
     */
    fun getSensorLight() {

        val byteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.SENSOR_LIGHT + "AA" + "000000" + CRC + SerialManager.FOOT
        )
        serialManager.write(byteArray)

    }

    /**
     * 读取崩机  版本
     */
    fun getPumpMotorVer() {

        val byteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.PUMP_MOTOR_VER + "AA" + "000000" + CRC + SerialManager.FOOT
        )
        serialManager.write(byteArray)

    }

    /**
     * 设置崩机  版本
     */
    fun setPumpMotorVer(isNew: Boolean) {

        val byteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.PUMP_MOTOR_VER + (if (isNew) "01" else "00") + "000000" + CRC + SerialManager.FOOT
        )
        serialManager.write(byteArray)

    }

    /**
     * 设置参数(手动调试)
     */
    suspend fun setParam(
        cleanDurationA: String,
        cleanDurationB: String,
        addDurationA: String,
        addDurationB: String
    ) {
        //清洗时间
        val cleanA = (if (cleanDurationA.toIntOrNull() == null) 0 else cleanDurationA.toInt())
        val cleanB = (if (cleanDurationB.toIntOrNull() == null) 0 else cleanDurationB.toInt())

        var byteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.CMD_SET_CLEAN_DURATION + ByteUtil.intToHex4(
                cleanA
            ) + ByteUtil.intToHex4(cleanB) + CRC + SerialManager.FOOT
        )
        serialManager.write(byteArray)

        delay(50)

        //进清洗液时间
        val addA = (if (addDurationA.toIntOrNull() == null) 0 else addDurationA.toInt())
        val addB = (if (addDurationB.toIntOrNull() == null) 0 else addDurationB.toInt())

        byteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.CMD_LIQUID_ENTER_DURATION + ByteUtil.intToHex4(
                addA
            ) + ByteUtil.intToHex4(addB) + CRC + SerialManager.FOOT
        )
        serialManager.write(byteArray)
        delay(50)
    }

    /**
     * 设置 高级参数
     */
    suspend fun setAdvParam(advParamModel: AdvParamModel) {
        //排空电机速度
        var byteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.EMPTY_MOTOR_SPEED + ByteUtil.intToHex(
                moterSpeedConvert(advParamModel.emptySpeed.toInt())
            ) + "000000" + CRC + SerialManager.FOOT
        )
        serialManager.write(byteArray)
        delay(50)
        //排空抽提时间
        byteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.EMPTY_EXTRACT_DURATION + ByteUtil.intToHex4(
                advParamModel.emptyExtractDuration.toInt()
            ) + "0000" + CRC + SerialManager.FOOT
        )
        serialManager.write(byteArray)
        delay(50)
        //排空抽提间隔
        byteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.EMPTY_EXTRACT_INTERVAL + ByteUtil.intToHex4(
                advParamModel.emptyExtractInterval.toInt()
            ) + "0000" + CRC + SerialManager.FOOT
        )
        serialManager.write(byteArray)
        delay(50)
        //排空烘干时间
        byteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.EMPTY_DRYING_DURATION + ByteUtil.intToHex4(
                advParamModel.emptyDryingDuration.toInt()
            ) + "0000" + CRC + SerialManager.FOOT
        )
        serialManager.write(byteArray)
        delay(50)
        //清洗电机速度
        byteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.CLEAN_MOTOR_SPEED + ByteUtil.intToHex(
                moterSpeedConvert(advParamModel.cleanSpeed.toInt())
            ) + "000000" + CRC + SerialManager.FOOT
        )
        serialManager.write(byteArray)
        delay(50)
        //清洗烘干时间
        byteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.CLEAN_DRYING_DURATION + ByteUtil.intToHex4(
                advParamModel.cleanDryingDuration.toInt()
            ) + "0000" + CRC + SerialManager.FOOT
        )
        serialManager.write(byteArray)
        delay(50)
        //泄压时间
        byteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.DECOM_P_DURATION + ByteUtil.intToHex4(
                advParamModel.decompDuration.toInt()
            ) + "0000" + CRC + SerialManager.FOOT
        )
        serialManager.write(byteArray)

    }


    /**
     * 设置 抽提参数 （设备参数页面 使用）
     */
    suspend fun setExtractParam(
        extractDurA: String,
        extractIntA: String,
        speedA: String,
        extractDurB: String,
        extractIntB: String,
        speedB: String
    ) {
        //抽提时间
        var byteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.CMD_EXTRACT_DURATION + ByteUtil.intToHex4(
                extractDurA.toInt()
            ) + ByteUtil.intToHex4(extractDurB.toInt()) + CRC + SerialManager.FOOT
        )

        serialManager.write(byteArray)
        delay(50)
        //抽提间隔
        byteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.CMD_EXTRACT_INTERVAL + ByteUtil.intToHex4(
                extractIntA.toInt()
            ) + ByteUtil.intToHex4(extractIntB.toInt()) + CRC + SerialManager.FOOT
        )
        serialManager.write(byteArray)
        delay(50)
        //电机速度
        byteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.MOTOR_SPEED + ByteUtil.intToHex(
                moterSpeedConvert(speedA.toInt())
            ) + "00" + ByteUtil.intToHex(moterSpeedConvert(speedB.toInt())) + "00" + CRC + SerialManager.FOOT
        )
        serialManager.write(byteArray)
    }

    /**
     * 设定值、灵敏度
     */
    fun setValueAndSen(channelPosition: Int, setValue: Int, sensitivity: Int) {
        val cmd = when (channelPosition) {
            1 -> SerialManager.A_UP_SET
            2 -> SerialManager.A_DOWN_SET
            3 -> SerialManager.B_UP_SET
            else -> SerialManager.B_DOWN_SET
        }

        val byteArray: ByteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + cmd + ByteUtil.intToHex4(setValue) + ByteUtil.intToHex4(
                sensitivity
            ) + CRC + SerialManager.FOOT
        )
        serialManager.write(byteArray)
    }


    /**
     * 电磁阀设置
     */
    fun solenoidValveSetting(state1: Boolean, state2: Boolean, state3: Boolean, state4: Boolean) {
        val byteArray: ByteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.SV_TEST + ByteUtil.intToHex(if (state1) 1 else 0) + ByteUtil.intToHex(
                if (state2) 1 else 0
            ) + ByteUtil.intToHex(if (state3) 1 else 0) + ByteUtil.intToHex(if (state4) 1 else 0) + CRC + SerialManager.FOOT
        )
        serialManager.write(byteArray)
    }


    /**
     * 电机设置
     */
    fun motorSetting(enable: Boolean, mode: Int, speed: Int) {
        val byteArray: ByteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.MOTOR_SEN + ByteUtil.intToHex(mode) +
                    ByteUtil.intToHex(speed) + "00" + ByteUtil.intToHex(if (enable) 1 else 0) + CRC + SerialManager.FOOT
        )
        serialManager.write(byteArray)
    }


    /**
     * 测试模式
     */
    fun debugMode(state: Boolean) {
        val byteArray: ByteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.DEBUG_MODE + ByteUtil.intToHex(
                if (state) 1 else 0
            ) + "000000" + CRC + SerialManager.FOOT
        )
        serialManager.write(byteArray)
    }

    /**
     * 设置状态
     */
    fun setState(channel: Int, state: Int) {
        val byteArray: ByteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + (if (channel == 1) SerialManager.A_CMD else SerialManager.B_CMD) + ByteUtil.intToHex(
                state
            ) + "000000" + CRC + SerialManager.FOOT
        )
        serialManager.write(byteArray)
    }

    /**
     * 读取固件版本
     */
    fun getFirmVersion() {
        val byteArray: ByteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.FIRMWARW_VER + "AA000000" + CRC + SerialManager.FOOT
        )
        serialManager.write(byteArray)
    }

    /**
     * 校准 模式
     */
    fun setCalibrationState(state: Int) {
        SPUtils.getInstance().put("calibrationState", state)
        calibrationState.intValue = state
        if (state == Mul) {
            calculatePointFactor()
        }
    }


    /**
     * 计算 温度
     * temperature 从设备读取的温度
     */
    private fun getTemperature(temperature: String): String {
        var t = "0"
        val df = DecimalFormat("0.00")

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

        }
        Log.e("calculatePointFactor", "$pointA   $pointB")
    }


    /**
     * 设置 温度
     */
    fun setTemperature(setTemperature: String) {
        if (LimitUtil.isOverLimit(MyApp.getInstance(), setTemperature)) {
            return
        }
        if (stateA != Empty || stateB != Empty) {
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
                SerialManager.HEAD + SerialManager.CMD_SET_T + ByteUtil.intToHex(
                    beforeDecimal.toInt()
                ) + ByteUtil.intToHex(afterDecimal.toInt()) + "010000" + SerialManager.FOOT
            )
        } else {
            beforeDecimal = writeTemperature
            byteArray = ByteUtil.hexStringToByteArray(
                SerialManager.HEAD + SerialManager.CMD_SET_T + ByteUtil.intToHex(
                    beforeDecimal.toInt()
                ) + "00" + "010000" + SerialManager.FOOT
            )
        }

        serialManager.write(byteArray)

    }


    /**
     * 停止  控制温度
     */
    fun stopTemperature() {
        if (stateA != Empty || stateB != Empty) {
            Toast.makeText(
                MyApp.getInstance(),
                MyApp.getInstance().getString(R.string.edit_t_tip),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val byteArray: ByteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.CMD_SET_T + "00" + "00" + "000000" + SerialManager.FOOT
        )
        serialManager.write(byteArray)
    }


    /**
     * 设置介质
     * model  4硅油
     */
    fun setMedium(model: Int) {
        val byteArrayP: ByteArray = ByteUtil.hexStringToByteArray(
            SerialManager.HEAD + SerialManager.MEDIUM_VALUE + ByteUtil.intToHex(
                model
            ) + "00" + "000000" + SerialManager.FOOT
        )
        serialManager.write(byteArrayP)

        SPUtils.getInstance().put("mediumInfo", Gson().toJson(mediumList))
    }


    fun startDryingTimer(channel: Int) {
        // 安全停止现有计时器
        stopDryingTimer()

        timerDrying = object : CountDownTimer(advParamModel.dryingDuration.toInt() * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                cancel()
                // 完成操作
                timerDrying = null  // 清除引用
                if (channel == 1) {
                    stateA = TestState.Empty

                } else {
                    stateB = TestState.Empty

                }
                viewModelScope.launch {
                    if (channel == 1) {
                        setState(1, CMD_Stop)
                    } else {
                        setState(2, CMD_Stop)
                    }
                }
            }
        }.start()


    }

    fun stopDryingTimer() {
        timerDrying?.onFinish()
    }

    fun startDecomPTimer() {
        // 安全停止现有计时器
        stopDecomPTimer()

        timerDecomP = object : CountDownTimer(advParamModel.decompDuration.toInt() * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                cancel()
                // 完成操作
                timerDecomP = null  // 清除引用
                stateA = TestState.Empty
                stateB = TestState.Empty
                viewModelScope.launch {
                    setState(1, CMD_Stop)
                    delay(50)
                    setState(2, CMD_Stop)
                }
            }
        }.start()


    }

    fun stopDecomPTimer() {
        timerDecomP?.onFinish()
    }


    var otaController by mutableStateOf<OtaController?>(null)
        public set

    fun setFirmControl(path: String) {
        otaController = OtaController(
            serial = serialManager,
            firmware = File(path).readBytes(),
            fileName = "app.bin"
        )
    }
}



