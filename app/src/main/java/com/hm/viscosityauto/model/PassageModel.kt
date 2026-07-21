package com.hm.viscosityauto.model

import android.content.Context
import android.util.Log
import com.hm.viscosityauto.MyApp
import com.hm.viscosityauto.R
import com.hm.viscosityauto.utils.ToastUtil
import com.hm.viscosityauto.vm.TestState.Empty
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

data class PassageModel(
    var id: Int = 0,
    var number: String = "0",
    var duration: String = "0.0000",
    var temperature: String = "0.0",
    var viscosity: String = "0.00000",
    var constant: String = "0.0",
    var testCount: String = "4",
    var curNum: Int = 0,//当前是第几次
    var durationArray: ArrayList<DurationModel> = ArrayList(),
    var state: Int = Empty,//
    var keepTDuration: String = "0",//恒温时长

    var cleanTimes: String = "0",//清洗次数
    var curCleanNum: Int = 0,//当前是第几次清洗
    var cleanDuration: String = "0",//清洗时长
    var addDuration: String = "0",//加液时长
    var extractDuration: String = "0",//抽提时间
    var extractInterval: String = "0",//抽提间隔
    var motorSpeed: String = "0",//抽提间隔

    var time: String = "",
) {


    //计算粘度
    fun computeViscosity(): Boolean {
        val format = DecimalFormat("0.00000")

        var durationArrayOfAll = 0.0
        var durationArrayTemp = mutableListOf<Double>()
        durationArray.forEach() {
            if (!it.derelict) {
                durationArrayTemp.add(it.duration)
            }
        }



        durationArrayOfAll = format.format(durationArrayTemp.average()).toDouble()

        //在温度100～15°C测定粘度时，这个差数不应超过算术平均值的±0.5%;在低于15～-30°C测定粘度时，这个差数不应超过算术平均值的±1.5%。在低于-30°C测定粘度时，这个差数不应超过算术平均值的±2.5%。
        val allowableError = if (temperature.toFloat() > 15) {
            durationArrayOfAll * 0.005
        } else if (temperature.toFloat() < 15 && temperature.toFloat() > -30) {
            durationArrayOfAll * 0.015
        } else {
            durationArrayOfAll * 0.025
        }

        durationArray.forEach() {
            it.enable = (abs(it.duration - durationArrayOfAll) <= allowableError) && !it.derelict
        }

        val enableDurationArray: ArrayList<Double> = ArrayList()
        durationArray.forEach() {
            if (it.enable) {
                enableDurationArray.add(it.duration)
            }
        }
        Log.e("durationArray", "$allowableError  $durationArrayOfAll")
        if (enableDurationArray.isEmpty()) {
            ToastUtil.show(
                MyApp.getInstance(),
                MyApp.getInstance().getString(R.string.no_valid_data)
            )
            return false
        }
        duration = format.format(enableDurationArray.average()).toString()
        val formatViscosity = DecimalFormat("0.00000")
        viscosity = formatViscosity.format((duration.toFloat() * constant.toFloat()))
        return true
    }

    fun isReady(autoClean:Boolean): Boolean {
        return if (autoClean){
            !(testCount.toIntOrNull() == null || testCount.toInt() <= 0 || constant.toFloatOrNull() == null || constant.toFloat() <= 0
                    || cleanTimes.toIntOrNull() == null || cleanTimes.toInt() < 0 || cleanDuration.toIntOrNull() == null || cleanDuration.toInt() < 0 || addDuration.toIntOrNull() == null || addDuration.toInt() <= 0 || extractDuration.toIntOrNull() == null || extractDuration.toInt() <= 0
                    || extractInterval.toIntOrNull() == null || extractInterval.toInt() <= 0 || motorSpeed.toFloatOrNull() == null || motorSpeed.toInt() <= 0 || motorSpeed.toInt() > 60 || keepTDuration.toIntOrNull() == null || keepTDuration.toInt() < 0)

        }else{
            !(testCount.toIntOrNull() == null || testCount.toInt() <= 0 || constant.toFloatOrNull() == null || constant.toFloat() <= 0
                    || extractDuration.toIntOrNull() == null || extractDuration.toInt() <= 0
                    || extractInterval.toIntOrNull() == null || extractInterval.toInt() <= 0 || motorSpeed.toFloatOrNull() == null || motorSpeed.toInt() <= 0 || motorSpeed.toInt() > 60 || keepTDuration.toIntOrNull() == null || keepTDuration.toInt() < 0)

        }
    }

}

data class DurationModel(
    val duration: Double,
    var enable: Boolean = true,
    var derelict: Boolean = false,//是否遗弃
    var temperature: String = "0.000",
)

