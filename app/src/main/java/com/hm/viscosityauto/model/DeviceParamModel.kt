package com.hm.viscosityauto.model

import com.hm.viscosityauto.ui.page.lightValueMax
import com.hm.viscosityauto.ui.page.setValueMax

data class DeviceParamModel(
    val name: String = "",

    val aUp: String = "0",
    val aDown: String = "0",
    val bUp: String = "0",
    val bDown: String = "0",

    val aUpSet: String = "0",
    val aDownSet: String = "0",
    val bUpSet: String = "0",
    val bDownSet: String = "0",

    val aUpSensitivity: String = "0",
    val aDownSensitivity: String = "0",
    val bUpSensitivity: String = "0",
    val bDownSensitivity: String = "0",

    ){


    fun isError():Boolean{
        return  (name.isEmpty()||aUpSet.toIntOrNull()==null||aUpSensitivity.toIntOrNull()==null||bUpSet.toIntOrNull()==null||bUpSensitivity.toIntOrNull()==null||
                aDownSensitivity.toIntOrNull()==null||aDownSet.toIntOrNull()==null||bDownSensitivity.toIntOrNull()==null||bDownSet.toIntOrNull()==null)
    }
    fun isOverLimit():Boolean{
        return  (aUpSet.toInt()<0||aUpSet.toInt()> setValueMax ||aDownSet.toInt()<0||aDownSet.toInt()> setValueMax ||
            bUpSet.toInt()<0||bUpSet.toInt()> setValueMax ||bDownSet.toInt()<0||bDownSet.toInt()> setValueMax ||
                aUpSensitivity.toInt()<0||aUpSensitivity.toInt()> lightValueMax ||aDownSensitivity.toInt()<0||aDownSensitivity.toInt()> lightValueMax ||
                bUpSensitivity.toInt()<0||bUpSensitivity.toInt()> lightValueMax ||bDownSensitivity.toInt()<0||bDownSensitivity.toInt()> lightValueMax)
    }

}
