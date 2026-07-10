package com.hm.viscosityauto.model

import com.hm.viscosityauto.ui.page.lightValueMax
import com.hm.viscosityauto.ui.page.lightValueMin
import com.hm.viscosityauto.ui.page.setValueMax
import com.hm.viscosityauto.ui.page.setValueMin

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
        return  (aUpSet.toInt() !in setValueMin..setValueMax ||aDownSet.toInt() !in setValueMin..setValueMax  ||
            bUpSet.toInt() !in setValueMin..setValueMax  ||bDownSet.toInt() !in setValueMin..setValueMax  ||
                aUpSensitivity.toInt()<lightValueMin||aUpSensitivity.toInt()> lightValueMax ||aDownSensitivity.toInt()<lightValueMin||aDownSensitivity.toInt()> lightValueMax ||
                bUpSensitivity.toInt()<lightValueMin||bUpSensitivity.toInt()> lightValueMax ||bDownSensitivity.toInt()<lightValueMin||bDownSensitivity.toInt()> lightValueMax)
    }



}
