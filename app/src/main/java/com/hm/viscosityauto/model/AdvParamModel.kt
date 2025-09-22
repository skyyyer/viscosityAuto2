package com.hm.viscosityauto.model

data class AdvParamModel(
    val emptySpeed:String = "5",
    val emptyExtractDuration:String = "5",
    val emptyExtractInterval:String = "10",
    val emptyDryingDuration:String = "30",

    val cleanSpeed:String = "50",
    val cleanDryingDuration:String = "60",

    val decompDuration:String = "50",//单独泄压时间

    val dryingDuration:String = "50",//单独烘干时间
    ){

    fun isOk(): Boolean {

        return !(emptySpeed.toIntOrNull() == null || emptySpeed.toInt() <= 0  || emptySpeed.toInt() > 60 || emptyExtractDuration.toFloatOrNull() == null || emptyExtractDuration.toFloat() <= 0
                || emptyExtractInterval.toIntOrNull() == null || emptyExtractInterval.toInt() < 0   || emptyDryingDuration.toIntOrNull() == null || emptyDryingDuration.toInt() < 0
                || cleanSpeed.toIntOrNull() == null || cleanSpeed.toInt() <= 0 || cleanSpeed.toInt() > 60 || cleanDryingDuration.toIntOrNull() == null || cleanDryingDuration.toInt() <= 0||
                decompDuration.toIntOrNull() == null ||decompDuration.toInt() <= 0||dryingDuration.toIntOrNull() == null || dryingDuration.toInt() <= 0)
    }


}

