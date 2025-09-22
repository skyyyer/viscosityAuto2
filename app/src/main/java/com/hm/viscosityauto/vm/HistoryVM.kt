package com.hm.viscosityauto.vm

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.serialport.SerialPort
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.hm.viscosityauto.MyApp
import com.hm.viscosityauto.R
import com.hm.viscosityauto.http.HttpUrl
import com.hm.viscosityauto.http.RetrofitClient
import com.hm.viscosityauto.model.Detail
import com.hm.viscosityauto.model.DurationModel
import com.hm.viscosityauto.model.UploadBean
import com.hm.viscosityauto.room.AppDatabase
import com.hm.viscosityauto.room.admin.AdminRecords
import com.hm.viscosityauto.room.audit.AuditRecords
import com.hm.viscosityauto.room.test.TestRecords
import com.hm.viscosityauto.ui.view.LoadingDialog
import com.hm.viscosityauto.utils.ComputeUtils
import com.hm.viscosityauto.utils.ExportDataUtil
import com.hm.viscosityauto.utils.FileUtil
import com.hm.viscosityauto.utils.NetworkUtil
import com.hm.viscosityauto.utils.SPUtils
import com.hm.viscosityauto.utils.StringUtils
import com.hm.viscosityauto.utils.TimeUtils
import com.hm.viscosityauto.utils.ToastUtil
import com.hm.viscosityauto.utils.UploadUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.util.HashMap

class HistoryVM : ViewModel() {

    val admin =
        Gson().fromJson(
            SPUtils.getInstance().getString("adminInfo", ""),
            AdminRecords::class.java
        )

    //数据库
    private var DB: AppDatabase = AppDatabase.getDatabase(MyApp.getInstance())

    //试验记录列表
    var recordsList = mutableStateListOf<TestRecords>()

    //记录页码
    var recordsPage = 0
    var limit = 20
    var date = ""


    //初始化串口 用于打印的 后续可以将Act中的输出流对象换成这里的输出流对象
    private lateinit var mSerialPort: SerialPort //串口对象
    private var mOutputStream: OutputStream? = null //串口的输出流对象 用于发送指令


    init {
        initPrintPort()
//        var durationArray: ArrayList<DurationModel> = ArrayList()
//        durationArray.add(DurationModel(203f,true))
//        durationArray.add(DurationModel(103f,false))
//        durationArray.add(DurationModel(203f,true))
//        durationArray.add(DurationModel(203f,true))
//        durationArray.add(DurationModel(403f,false))
//        durationArray.add(DurationModel(203f,true))
//        durationArray.add(DurationModel(203f,true))
//
//        recordsList.add(
//            TestRecords(
//                testNum ="555",
//                duration = "200",
//                temperature = "55",
//                constant = "0.22",
//                viscosity = "23",
//                date = "2025-05-23",
//                time = "12-05-23",
//                durationArray = Gson().toJson(durationArray),
//                tester ="admin"
//            )
//        )

    }


    suspend fun getTestData() {
        val lists: SnapshotStateList<TestRecords> =
            DB.testDao().getPageTestRecord(recordsPage * limit, limit, date)
                .toMutableStateList()

        if (recordsPage == 0) {
            recordsList = lists
        } else {
            recordsList.addAll(lists)

        }
    }


    /**
     * 初始化 打印串口
     */
    private fun initPrintPort() {

        try {
            val device = File("/dev/ttyS7")
            mSerialPort = SerialPort // 串口对象
                .newBuilder(device, 9600) // 串口地址地址，波特率
                .dataBits(8) // 数据位,默认8；可选值为5~8
                .stopBits(1) // 停止位，默认1；1:1位停止位；2:2位停止位
                .parity(0) // 校验位；0:无校验位(NONE，默认)；1:奇校验位(ODD);2:偶校验位(EVEN)
                .build() // 打开串口并返回
            mOutputStream = mSerialPort.outputStream

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    fun delAllTestData() {
        viewModelScope.launch(Dispatchers.Main) {
            DB.testDao().deleteAll()
        }
        recordsList.clear()
    }

    fun delTestDatas(models: List<TestRecords>) {
        val ids = mutableListOf<Long>()
        models.forEach {
            ids.add(it.id)
        }
        viewModelScope.launch(Dispatchers.IO) {
            DB.testDao().deleteByIds(ids)
        }
        recordsList.removeAll(models)
    }


    @SuppressLint("HardwareIds")
    fun upLoadData(context: Context, testRecord: TestRecords) {
        if (NetworkUtil.isNetworkAvailable(MyApp.getInstance())) {
            LoadingDialog.show(context.getString(R.string.uploading))
            val devId = Settings.Secure.getString(
                MyApp.getInstance().contentResolver, Settings.Secure.ANDROID_ID
            )
            val uploadPath = SPUtils.getInstance()
                .getString(
                    "uploadPath",
                    "http://39.98.237.174:80/control/upload/data/uploadData"
                )
            val uploadUser = SPUtils.getInstance().getString("uploadUser", "ceshi")
            val uploadPwd = SPUtils.getInstance().getString("uploadPwd", "123456")

            val detail = Detail(
                jiancedidian = "QD",
//                jiancejieguo = if (testRecord.isPass) "合格" else "不合格",
//                jianceren = testRecord.user,
//                jianceriqi = testRecord.date,
//                jiancezhi = testRecord.result,
//                jiancexiangmu = ConfigType.fromType(testRecord.testType)!!.name + " "+testRecord.filterInfo,
//                yangpinbianhao = testRecord.num,
//                yangpinmingcheng = testRecord.name
            )


            val uploadBean = UploadBean(
                dwmc = "恒美",
                password = uploadPwd,
                username = uploadUser,
                yqbh = devId,
                details = listOf(detail)
            )
            try {
                viewModelScope.launch(Dispatchers.IO) {
                    val result =
                        UploadUtil().readContentFromPost(uploadPath, Gson().toJson(uploadBean))

                    withContext(Dispatchers.Main) {
                        when (result) {
                            "\"success\"" -> {
                                ToastUtil.show(context, context.getString(R.string.upload_success))
                            }

                            "\"-2\"" -> {
                                ToastUtil.show(
                                    context,
                                    context.getString(R.string.name_or_pwd_error)
                                )
                            }

                            "\"-3\"" -> {
                                ToastUtil.show(context, context.getString(R.string.data_null))
                            }

                            else -> {
                                ToastUtil.show(context, context.getString(R.string.upload_fail))
                            }
                        }
                        LoadingDialog.dismiss()
                    }
                }

            } catch (e: Exception) {
                ToastUtil.show(context, context.getString(R.string.net_error))
                LoadingDialog.dismiss()
            }

        } else {
            ToastUtil.show(context, context.getString(R.string.wlan_no_connect_tip))
        }
    }


    /**
     * 打印实验结果
     */
    fun printData(
        context: Context,
        model: TestRecords
    ) {

        val list: MutableList<ByteArray> = java.util.ArrayList()
        list.add(
            StringUtils.str2Bytes(
                "***********" + context
                    .getString(R.string.test_result) + "************" + "\r\n"
            )
        )

        list.add(
            StringUtils.str2Bytes(
                context
                    .getString(R.string.time) + ":    " + model.time + "\r\n" + "\r\n"
            )
        )
        list.add(
            StringUtils.str2Bytes(
                context
                    .getString(R.string.tester) + ":    " + model.tester + "\r\n" + "\r\n"
            )
        )


        list.add(
            StringUtils.str2Bytes(
                context.getString(R.string.number) + ":    " + model.testNum + "\r\n"
            )
        )
        list.add(
            StringUtils.str2Bytes(
                context
                    .getString(R.string.duration) + ":    " + model.duration + " s\r\n"
            )
        )

        if (model.durationArray.isNotEmpty()) {
            val lists = Gson().fromJson(
                model.durationArray,
                Array<DurationModel>::class.java
            ).toList()

            lists.forEachIndexed { index, it ->
                if (!it.derelict){
                    list.add(
                        StringUtils.str2Bytes(
                            "    " +
                                    context.getString(R.string.number_start) + (index + 1) + (if (SPUtils.getInstance()
                                    .getString("language", LANGUAGE_ZH) == LANGUAGE_ZH
                            ) context.getString(
                                R.string.number_end
                            ) else " ") +  "%.2f".format(it.duration) + " s\r\n"
                        )
                    )

                }
            }
        }



        list.add(
            StringUtils.str2Bytes(
                context
                    .getString(R.string.temperature) + ":    " + model.temperature + " ℃\r\n"
            )
        )

        list.add(
            StringUtils.str2Bytes(
                context
                    .getString(R.string.viscosity_constant) + ":    " + model.constant + " mm2/s2\r\n" + "\r\n"
            )
        )

        list.add(
            StringUtils.str2Bytes(
                context
                    .getString(R.string.viscosity) + ":    " + model.viscosity + " mm2/s\r\n"
            )
        )

        list.add(("*******************************" + "\r\n").toByteArray(charset("GB2312")))
        list.add((" " + "\r\n").toByteArray(charset("GB2312")))
        list.add((" " + "\r\n").toByteArray(charset("GB2312")))
        list.add((" " + "\r\n").toByteArray(charset("GB2312")))

        viewModelScope.launch {

            withContext(Dispatchers.IO) {

                try {
                    list.forEach() {
                        mOutputStream?.write(it)
                    }

                } catch (e: IOException) {
                    Log.e("print", e.message.toString())
                    e.printStackTrace()
                }


            }
        }


    }

    /**
     * 导出数据
     */
    fun exportData(context: Context, list: List<TestRecords>) {
        val path = FileUtil.getStoragePath(context, true)
        Log.e("getStoragePath", path)

        if (path.isEmpty()) {
            Toast.makeText(
                MyApp.getInstance(),
                context.getText(R.string.export_fail_no_path),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        LoadingDialog.show(context.getString(R.string.exporting))



        viewModelScope.launch(Dispatchers.IO) {
            val success = ExportDataUtil().download(context, path, list)

            withContext(Dispatchers.Main) {
                LoadingDialog.dismiss()
                if (success) {
                    Toast.makeText(
                        MyApp.getInstance(),
                        context.getText(R.string.export_success),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        MyApp.getInstance(),
                        context.getText(R.string.export_fail),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }


    }

    /**
     * 导出全部数据
     */
    fun exportDataAll(context: Context) {
        val path = FileUtil.getStoragePath(context, true)
        Log.e("getStoragePath", path)

        if (path.isEmpty()) {
            Toast.makeText(
                context,
                context.getText(R.string.export_fail_no_path),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        LoadingDialog.show(context.getString(R.string.exporting))



        viewModelScope.launch(Dispatchers.IO) {
            val list = DB.testDao().getTestRecords()

            val success = ExportDataUtil().download(context, path, list)

            withContext(Dispatchers.Main) {
                LoadingDialog.dismiss()
                if (success) {
                    Toast.makeText(
                        context,
                        context.getText(R.string.export_success),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        context.getText(R.string.export_fail),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        }


    }

}