package com.hm.viscosityauto.model

import com.hm.viscosityauto.ui.page.lightValueMax
import com.hm.viscosityauto.ui.page.lightValueMin
import com.hm.viscosityauto.ui.page.setValueMax
import com.hm.viscosityauto.ui.page.setValueMin

data class ExtractModel(
    val extracttime : String = "5",
    val extracttimejg: String = "5",
    val motorspeed: String = "5",

    ){


    fun isError():Boolean{
        return  (extracttime.toIntOrNull()==null||extracttimejg.toIntOrNull()==null||motorspeed.toIntOrNull()==null)
    }


}
