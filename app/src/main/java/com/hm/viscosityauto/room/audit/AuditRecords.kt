package com.hm.viscosityauto.room.audit

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar


@Entity(tableName = "audit")
data class AuditRecords (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0 ,

    @ColumnInfo
    var date: String,

    @ColumnInfo
    var time: String,

    @ColumnInfo
    var user: String,

    @ColumnInfo
    var des: String,

    @ColumnInfo
    var role: Int
)