package com.hm.viscosityauto.room.admin

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin")
data class AdminRecords (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0, //主键

    @ColumnInfo
    var name: String = "",

    @ColumnInfo
    var pwd: String = "",

    @ColumnInfo
    var role: Int = 0
)