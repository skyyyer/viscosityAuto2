package com.hm.viscosityauto.vm

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hm.viscosityauto.R
import com.hm.viscosityauto.room.AppDatabase
import com.hm.viscosityauto.room.audit.AuditRecords
import com.hm.viscosityauto.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class Operate(val id: Int, val des: String) {
    Test(1, MyApp.getInstance().getString(R.string.test)),//测试
    AddAdmin(2, MyApp.getInstance().getString(R.string.add_admin)),//
    DelAdmin(3, MyApp.getInstance().getString(R.string.del_admin)),
    EditAdmin(4, MyApp.getInstance().getString(R.string.edit_admin)),
    EditTime(5, MyApp.getInstance().getString(R.string.time_edit)),
    ChangeLanguage(5, MyApp.getInstance().getString(R.string.change_language)),
    DelTestRecord(6, MyApp.getInstance().getString(R.string.del_test_data)),
    DelAuditRecord(7, MyApp.getInstance().getString(R.string.del_audit)),
    EditUpload(8, MyApp.getInstance().getString(R.string.edit_upload_info))
}

class AuditVM : ViewModel() {


    //数据库
    private lateinit var DB: AppDatabase

    //试验记录列表
    var recordsList = mutableStateListOf<AuditRecords>()

    //记录页码
    var recordsPage = 0
    private var limit = 20

    var date = ""
    var admin = ""

    fun initDB() {
        DB = AppDatabase.getDatabase(MyApp.getInstance())
    }


    suspend fun getTestData() {
        val lists: SnapshotStateList<AuditRecords> =
            DB.auditDao().getPageRecord(recordsPage * limit, limit, date, admin)
                .toMutableStateList()

        if (recordsPage == 0) {
            recordsList = lists
        } else {
            recordsList.addAll(lists)

        }

    }


    fun delAllTestData() {
        viewModelScope.launch(Dispatchers.Main) {
            DB.auditDao().deleteAll()
        }
        recordsList.clear()
    }

    fun delTestData(model: AuditRecords) {
        viewModelScope.launch(Dispatchers.Main) {
            DB.auditDao().delete(model)
        }
        recordsList.remove(model)
    }


}