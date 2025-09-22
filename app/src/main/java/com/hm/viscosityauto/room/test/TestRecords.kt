package com.hm.viscosityauto.room.test

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "test")
data class TestRecords (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0, //主键

    @ColumnInfo
    var testNum: String = "",

    @ColumnInfo
    var date: String = "",

    @ColumnInfo
    var time: String = "",

    @ColumnInfo
    var temperature: String = "",

    @ColumnInfo
    var duration: String = "",

    @ColumnInfo
    var viscosity: String = "",

    @ColumnInfo
    var constant: String = "",

    @ColumnInfo
    var durationArray: String = "",

    @ColumnInfo
    var tester: String = "",
)