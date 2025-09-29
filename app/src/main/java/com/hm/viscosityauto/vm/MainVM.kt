package com.hm.viscosityauto.vm

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.NetworkSpecifier
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.PatternMatcher
import android.provider.Settings
import android.serialport.SerialPort
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asi.nav.Nav
import com.azhon.appupdate.manager.DownloadManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hm.viscosity.model.MediumModel
import com.hm.viscosityauto.GlobalState
import com.hm.viscosityauto.LoginPageRoute
import com.hm.viscosityauto.MainPageRoute
import com.hm.viscosityauto.MyApp
import com.hm.viscosityauto.R
import com.hm.viscosityauto.http.HttpUrl
import com.hm.viscosityauto.http.RetrofitClient
import com.hm.viscosityauto.model.DurationModel
import com.hm.viscosityauto.room.AppDatabase
import com.hm.viscosityauto.room.admin.AdminRecords
import com.hm.viscosityauto.room.audit.AuditRecords
import com.hm.viscosityauto.ui.view.LoadingDialog
import com.hm.viscosityauto.utils.ByteUtil
import com.hm.viscosityauto.utils.ComputeUtils
import com.hm.viscosityauto.utils.NetworkUtil
import com.hm.viscosityauto.utils.PrintUtils
import com.hm.viscosityauto.utils.SPUtils
import com.hm.viscosityauto.utils.SerialPortManager
import com.hm.viscosityauto.utils.SerialPortManager.listSerialPorts
import com.hm.viscosityauto.utils.StringUtils
import com.hm.viscosityauto.utils.TimeUtils
import com.hm.viscosityauto.utils.ToastUtil
import com.iwdael.wifimanager.IWifi
import com.iwdael.wifimanager.IWifiManager
import com.iwdael.wifimanager.WifiManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.lang.reflect.Method


const val LANGUAGE_ZH = "zh"
const val LANGUAGE_EN = "en"


const val MaxT = 100 //最大温度


class MainVM : ViewModel() {

    var week by mutableStateOf("")
    var date by mutableStateOf("")
    var time by mutableStateOf("")

    var versionName = mutableStateOf("1.0.0")
    var versionCode = mutableIntStateOf(1)
    private var newApkUrl = mutableStateOf("")
    private var newApkVersionCode = mutableIntStateOf(1)


    var newApkPath = mutableStateOf("")
    var helpVideoPath: String = "/sdcard/DCIM/1.mp4"
    var helpVideoENPath: String = "/sdcard/DCIM/1_en.mp4"

    //登录信息
    var loginInfo by mutableStateOf(AdminRecords())

    // 用户信息
    var adminInfo = mutableStateOf(AdminRecords())
    var adminList = mutableStateListOf<AdminRecords>()


    //初始化串口 用于打印的 后续可以将Act中的输出流对象换成这里的输出流对象
    private lateinit var mSerialPort: SerialPort //串口对象
    private var mOutputStream: OutputStream? = null //串口的输出流对象 用于发送指令

    //语言
    var language = mutableStateOf(SPUtils.getInstance().getString("language", LANGUAGE_ZH))

    //自动打印
    var autoPrint = mutableStateOf(SPUtils.getInstance().getBoolean("autoPrint", true))

    //自动上传
    var autoUpload = mutableStateOf(SPUtils.getInstance().getBoolean("autoUpload", true))

    //自动清洗
    var autoClean = mutableStateOf(SPUtils.getInstance().getBoolean("autoClean", true))
    //自动排空
    var autoEmpty = mutableStateOf(SPUtils.getInstance().getBoolean("autoEmpty", true))

    //设备串口通信
    private var serialPortManager: SerialPortManager? = null


    private lateinit var wifiManager: IWifiManager

    // wifi 开关状态
    var wifiState = mutableStateOf(true)

    // wifi 扫描状态
    var wifiScanState = mutableIntStateOf(0)// 0 未扫描  1 扫描中

    //wifi列表
    var wifiList = mutableStateListOf<IWifi>()

    //已连接的wifi
    var wifiConnectedList = mutableStateListOf<IWifi>()

    //数据库
    private lateinit var DB: AppDatabase


    var uploadPath = mutableStateOf(
        SPUtils.getInstance()
            .getString("uploadPath", "http://39.98.237.174:80/control/upload/data/uploadData")
    )
    var uploadUser = mutableStateOf(SPUtils.getInstance().getString("uploadUser", "ceshi"))
    var uploadPwd = mutableStateOf(SPUtils.getInstance().getString("uploadPwd", "123456"))




    /**
     * 初始化
     * -数据库
     * -打印串口
     * -通道计时器
     */
    fun init(context: Context) {

        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                date = TimeUtils.timestampToDate()
                time = TimeUtils.timestampToTime()
                week = TimeUtils.timestampToDayOfWeek(context)
                delay(1000)
            }

        }

        listSerialPorts()
        initWifi(context)

        viewModelScope.launch {
            launch { initSerialPort() }
        }

    }


    private suspend fun initSerialPort(){
        serialPortManager =
            SerialPortManager(PATH, 9600, object :SerialPortManager.OnDataReceivedListener{
                override fun onTemperatureReceived(temperature: String?) {
                }

                override fun onLightStateReceived(state: Boolean?) {
                }

                override fun onHeatingState(state: Int) {
                }

                override fun onADeviceState(state: Int) {
                }

                override fun onBDeviceState(state: Int) {
                }

                override fun onADetectedValue(valueUp: Int, valueDown: Int) {
                }

                override fun onBDetectedValue(valueUp: Int, valueDown: Int) {
                }
            })

        delay(100)
        setLightState(true)
        delay(1000)
        serialPortManager?.close()
        serialPortManager = null // 重要！解除引用

    }

    /**
     * 加载本地 设置数据
     */
    fun getLocalSetting() {
        DB = AppDatabase.getDatabase(MyApp.getInstance())

        viewModelScope.launch(Dispatchers.IO) {
            if (DB.adminDao().getAdminCount() == 0) {
                val adminRecords = AdminRecords()
                adminRecords.name = "admin"
                adminRecords.pwd = "123456"
                adminRecords.role = 1
                DB.adminDao().addAdmin(adminRecords)
            }

            if (SPUtils.getInstance().getString("adminInfo", "").isNotEmpty()) {
                loginInfo =
                    Gson().fromJson(
                        SPUtils.getInstance().getString("adminInfo", ""),
                        AdminRecords::class.java
                    )

                val adminModel:AdminRecords? = DB.adminDao().getAdmin(loginInfo.name)
                if (adminModel == null || adminModel.name.isEmpty()) {
                    Nav.offAllTo(LoginPageRoute.route)
                }else{
                    if (adminModel.pwd == loginInfo.pwd) {
                        adminInfo.value = adminModel
                        adminList = DB.adminDao().getAdmins().toMutableStateList()
                        Nav.offAllTo(MainPageRoute.route)
                    } else {
                        Nav.offAllTo(LoginPageRoute.route)
                    }
                }
            } else {
                Nav.offAllTo(LoginPageRoute.route)
            }
        }


    }

    /**
     * 初始化用户信息
     */
    fun adminLogin(context: Context,userName: String, pwd: String, autoLogin: Boolean) {

        var adminModel: AdminRecords?
        viewModelScope.launch(Dispatchers.Main) {
            adminModel = DB.adminDao().getAdmin(userName)
            Log.e("adminModel", "$adminModel")

            if (adminModel == null || adminModel!!.name.isEmpty()) {
                Toast.makeText(
                    context,
                    context.getText(R.string.admin_not_find),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (adminModel!!.pwd != pwd) {
                    Toast.makeText(
                        context,
                        context.getText(R.string.pwd_error),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (autoLogin) {
                        SPUtils.getInstance().put("adminInfo", Gson().toJson(adminModel))
                    }else{
                        SPUtils.getInstance().put("adminInfo", Gson().toJson(adminModel!!.copy(pwd="")))
                    }
                    adminInfo.value = adminModel!!
                    adminList = DB.adminDao().getAdmins().toMutableStateList()
                    Nav.offAllTo(MainPageRoute.route)
                }

            }

        }
    }


    /**
     * 退出登录
     */
    fun logout() {
        adminInfo.value = AdminRecords()
//        SPUtils.getInstance().clear()
        Nav.offAllTo(LoginPageRoute.route)
    }

    /**
     * 添加用户
     */
    fun addAdmin(adminRecords: AdminRecords) {

        viewModelScope.launch(Dispatchers.Main) {
            DB.adminDao().addAdmin(adminRecords)
            adminList.add(0, adminRecords)
            DB.auditDao().insert(
                AuditRecords(
                    user = adminInfo.value.name,
                    role = adminInfo.value.role,
                    date = TimeUtils.timestampToDate(System.currentTimeMillis()),
                    time = TimeUtils.timestampToTime(System.currentTimeMillis()),
                    des = Operate.AddAdmin.des
                )
            )
        }
    }

    /**
     * 添加用户
     */
    fun delAdmin(adminRecords: AdminRecords) {
        viewModelScope.launch(Dispatchers.Main) {
            DB.adminDao().deleteAdmin(adminRecords)
            adminList.remove(adminRecords)

            DB.auditDao().insert(
                AuditRecords(
                    user = adminInfo.value.name,
                    role = adminInfo.value.role,
                    date = TimeUtils.timestampToDate(System.currentTimeMillis()),
                    time = TimeUtils.timestampToTime(System.currentTimeMillis()),
                    des = Operate.DelAdmin.des
                )
            )

        }
    }

    /**
     * 修改用户
     */
    fun editAdmin(adminRecord: AdminRecords) {
        viewModelScope.launch(Dispatchers.Main) {
            DB.adminDao().updateData(adminRecord)
            val filter = adminList.first { it.id == adminRecord.id }
            val index = adminList.indexOf(filter)
            adminList[index] = adminRecord

            DB.auditDao().insert(
                AuditRecords(
                    user = adminInfo.value.name,
                    role = adminInfo.value.role,
                    date = TimeUtils.timestampToDate(System.currentTimeMillis()),
                    time = TimeUtils.timestampToTime(System.currentTimeMillis()),
                    des = Operate.EditAdmin.des
                )
            )
        }
    }

    /**
     * 修改上传配置
     */
    fun editUploadInfo(path: String, name: String, pwd: String) {
        viewModelScope.launch(Dispatchers.Main) {
            uploadPath.value = path
            uploadUser.value = name
            uploadPwd.value = pwd

            SPUtils.getInstance().put("uploadPath", path)
            SPUtils.getInstance().put("uploadUser", name)
            SPUtils.getInstance().put("uploadPwd", pwd)

            DB.auditDao().insert(
                AuditRecords(
                    user = adminInfo.value.name,
                    role = adminInfo.value.role,
                    date = TimeUtils.timestampToDate(System.currentTimeMillis()),
                    time = TimeUtils.timestampToTime(System.currentTimeMillis()),
                    des = Operate.EditUpload.des
                )
            )
            Toast.makeText(
                MyApp.getInstance(),
                MyApp.getInstance().getString(R.string.save_success),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    /**
     * 修改上传配置
     */
    fun editTime(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            TimeUtils.setSystemTime(year, month, day, hour, minute, second, true)

            DB.auditDao().insert(
                AuditRecords(
                    user = adminInfo.value.name,
                    role = adminInfo.value.role,
                    date = TimeUtils.timestampToDate(System.currentTimeMillis()),
                    time = TimeUtils.timestampToTime(System.currentTimeMillis()),
                    des = Operate.EditTime.des
                )
            )
            Toast.makeText(
                MyApp.getInstance(),
                MyApp.getInstance().getString(R.string.save_success),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * wifi
     */
    fun initWifi(context: Context) {

        wifiManager = WifiManager.create(context)
        wifiState.value = wifiManager.isOpened
        wifiManager.setOnWifiChangeListener { it ->
            Log.e("setOnWifiChangeListener",it.toString())
            wifiScanState.intValue = 0

            val connectList: MutableList<IWifi> = ArrayList()
            val enableList: MutableList<IWifi> = ArrayList()

            it.forEach {
                if (it.isConnected) {
                    connectList.add(it)
                } else {
                    enableList.add(it)
                }
            }
            wifiConnectedList.clear()
            wifiConnectedList.addAll(connectList.toMutableStateList())
            wifiList.clear()
            wifiList.addAll(enableList.toMutableStateList())


        }
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        connectivityManager!!.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // WiFi 连接成功
                Log.d("WiFiStatus", "网络已连接")
                LoadingDialog.dismiss()
            }

            override fun onUnavailable() {
                super.onUnavailable()
                LoadingDialog.dismiss()
            }

            override fun onLost(network: Network) {
                // WiFi 断开
                Log.d("WiFiStatus", "网络断开")
            }
        })

        if (wifiManager.isOpened) {
            scanWIFI()
        }
    }

    /**
     * 扫描wifi
     */
    fun scanWIFI() {
        if (wifiScanState.intValue == 1) {
            return
        }

        wifiScanState.intValue = 1

        wifiManager.scanWifi()
    }

    /**
     * 连接wifi
     */
    fun connectWIFI(context:Context,wifi: IWifi, pwd: String = "") {

        LoadingDialog.show(context.getString(R.string.connecting))
        LoadingDialog.show(context.getString(R.string.connecting))
        viewModelScope.launch {
            delay(15*1000)
            withContext(Dispatchers.Main){
                if (LoadingDialog.getShowState()){
                    LoadingDialog.dismiss()
                    ToastUtil.show(context,context.getString(R.string.connect_error))
                }
            }
        }

        if (wifi.isEncrypt) {
            wifiManager.connectEncryptWifi(wifi, pwd)
        } else if (wifi.isSaved) {
            wifiManager.connectSavedWifi(wifi)
        } else {
            wifiManager.connectOpenWifi(wifi)
        }
//

    }


//    @RequiresApi(api = Build.VERSION_CODES.Q)
//    @Throws(InterruptedException::class)
//    private fun connectAP_Q(ssid: String, pass: String) {
//        val connectivityManager =
//            MyApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val specifier: NetworkSpecifier = WifiNetworkSpecifier.Builder()
//            .setSsid(ssid)
////            .setSsidPattern(PatternMatcher(ssid, PatternMatcher.PATTERN_PREFIX))
//            .setWpa2Passphrase(pass)
//            .build()
//        //创建一个请求
//        val request = NetworkRequest.Builder()
//            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI) //创建的是WIFI网络。
//            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED) //网络不受限
//            .addCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED) //信任网络，增加这个连个参数让设备连接wifi之后还联网。
//            .setNetworkSpecifier(specifier)
//            .build()
//        connectivityManager.requestNetwork(request, object : ConnectivityManager.NetworkCallback() {
//            override fun onAvailable(network: Network) {
//                scanWIFI()
//            }
//
//            override fun onUnavailable() {
//            }
//        })
//    }


    /**
     * 断开wifi
     */
    fun disconnectWIFI() {
        wifiScanState.intValue = 0
        wifiManager.disConnectWifi()
        wifiConnectedList.clear()
    }

    /**
     * 打开wifi
     */
    fun openWifi() {
        wifiManager.openWifi()
        scanWIFI()
    }

    /**
     * 关闭wifi
     */
    fun closeWIFI() {
        disconnectWIFI()
    }

    fun setVersion(version: PackageInfo) {
        this.versionName.value = version.versionName
        this.versionCode.value = version.versionCode
    }




    fun setLanguage(language: String) {
        this.language.value = language
        SPUtils.getInstance().put("language", language)
    }

    fun setAutoUpload(state: Boolean) {
        this.autoUpload.value = state
        SPUtils.getInstance().put("autoUpload", state)
    }

    fun setAutoClean(state: Boolean) {
        if (state){
            setAutoEmpty(true)
            this.autoClean.value = true
            SPUtils.getInstance().put("autoClean", true)
        }else{
            this.autoClean.value = false
            SPUtils.getInstance().put("autoClean", false)
        }

    }

    fun setAutoEmpty(state: Boolean) {
        if (state){
            this.autoEmpty.value = true
            SPUtils.getInstance().put("autoEmpty", true)
        }else{
            this.autoEmpty.value = false
            SPUtils.getInstance().put("autoEmpty", false)
            setAutoClean(false)
        }
    }


    fun setAutoPrint(state: Boolean) {
        this.autoPrint.value = state
        SPUtils.getInstance().put("autoPrint", state)
    }

    @SuppressLint("HardwareIds", "PrivateApi")
    fun getApp() {
        if (NetworkUtil.isNetworkAvailable(MyApp.getInstance())) {
            viewModelScope.launch(Dispatchers.Main) {
                val devId =
                    if (Build.VERSION.SDK_INT >= 28) {
                        try {
                            val c = Class.forName("android.os.SystemProperties")
                            val get: Method = c.getMethod("get", String::class.java)
                            get.invoke(c, "ro.serialno") as String
                        } catch (var4: Exception) {
                            ""
                        }
                    } else {
                        Build.SERIAL
                    }

                val map = mutableMapOf<String, String>()
                map["updateAPK"] =
                    SPUtils.getInstance().getString("token", "e4fec07c-8917-44ca-99f5-582daa869f02")
                map["acCode"] = devId

                try {
                    val model =
                        RetrofitClient.retrofit.create(HttpUrl::class.java).getByApp(map.toMap())

                    Log.e("result", model.toString())

                    if (model.data != null && model.data!!.apkRevision.toIntOrNull() != null) {
                        if (versionCode.intValue < model.data!!.apkRevision.toInt()) {
                            newApkUrl.value = model.data!!.apkOssUrl
                            newApkVersionCode.intValue = model.data!!.apkRevision.toInt()
                        }
                    }
                }catch (e:Exception){
                    Log.e("getApp","网络错误")
                }

            }
        }

    }

    /**
     * 设置 灯 开关
     */
    fun setLightState(state: Boolean) {

        val byteArray = ByteUtil.hexStringToByteArray(
            SerialPortManager.HEAD + SerialPortManager.CMD_LIGHT + (if (state) "01" else "00") + "000000" + SerialPortManager.CRC + SerialPortManager.FOOT
        )
        ByteUtil.printByteArray(byteArray)
        serialPortManager?.write(byteArray)

    }

    fun installApk(activity: Activity) {
        if (!NetworkUtil.isNetworkAvailable(activity)) {
            Toast.makeText(
                activity,
                activity.getString(R.string.wlan_no_connect_tip),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        //更新apk
        downloadApp(newApkUrl.value, newApkVersionCode.intValue, activity)
    }

    private fun downloadApp(dowloadUrl: String, versionCode: Int, activity: Activity) {
        val manager: DownloadManager = DownloadManager.Builder(activity)
            .apkUrl(dowloadUrl)
            .apkName("app.apk")
            .smallIcon(R.mipmap.ic_launcher) //设置了此参数，那么内部会自动判断是否需要显示更新对话框，否则需要自己判断是否需要更新
            .apkVersionCode(versionCode) //同时下面三个参数也必须要设置
            .apkVersionName("$versionCode.0")
            .apkSize("47")
            .apkDescription(activity.getString(R.string.new_version)) //省略一些非必须参数...
            .forcedUpgrade(true)
            .build()
        manager.download()
    }



}
