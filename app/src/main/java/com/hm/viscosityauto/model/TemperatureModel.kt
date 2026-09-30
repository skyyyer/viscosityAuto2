package com.hm.viscosityauto.model

data class TemperatureModel(
    val  temperature: String,
    val  time: String,
    val  state: Int,
    val  temperaturePoint: String = "0",
    )
