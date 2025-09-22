package com.hm.viscosityauto.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hm.viscosityauto.room.admin.AdminDao
import com.hm.viscosityauto.room.admin.AdminRecords
import com.hm.viscosityauto.room.audit.AuditDao
import com.hm.viscosityauto.room.audit.AuditRecords
import com.hm.viscosityauto.room.config.ConfigDao
import com.hm.viscosityauto.room.config.ConfigModel
import com.hm.viscosityauto.room.test.TestDao
import com.hm.viscosityauto.room.test.TestRecords


@Database(entities = [AdminRecords::class, TestRecords::class,AuditRecords::class,ConfigModel::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun adminDao(): AdminDao
    abstract fun testDao(): TestDao

    abstract fun auditDao(): AuditDao

    abstract fun configDao(): ConfigDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}