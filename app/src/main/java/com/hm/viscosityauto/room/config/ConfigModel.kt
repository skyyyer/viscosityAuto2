package com.hm.viscosityauto.room.config

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "config")
data class ConfigModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, //主键

    @ColumnInfo
    val name: String = "",

    @ColumnInfo
    val type: Int = 0,

    @ColumnInfo
    val prePressure: Int = 0,

    @ColumnInfo
    val preDuration: Int = 0,

    @ColumnInfo
    val minBubble: Int = 0,

    @ColumnInfo
    val maxBubble: Int = 0,


    )