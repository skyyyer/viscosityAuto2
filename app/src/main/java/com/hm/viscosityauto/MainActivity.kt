package com.hm.viscosityauto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hm.viscosityauto.ui.theme.ViscosityAutoTheme
import com.hm.viscosityauto.ui.view.ErrorView
import com.hm.viscosityauto.ui.view.LoadingDialog
import com.hm.viscosityauto.utils.SerialManager
import com.hm.viscosityauto.vm.MainVM
import me.rosuh.filepicker.config.FilePickerManager


object GlobalState {
    var isSetAdvParam by mutableStateOf(false)
    var lightState by mutableStateOf(false)

}

class MainActivity : ComponentActivity() {
    val mainVM: MainVM = MainVM()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        //隐藏状态栏
        val intent = Intent("hide.systemui")
        sendBroadcast(intent)
        //禁止滑动唤出状态栏
        val intent2 = Intent("com.zc.close_gesture")
        sendBroadcast(intent2)
        requestMyPermissions()


//        SerialManager
//            .getInstance(
//                "/dev/ttyS1",
//                9600
//            )
//            .initialize()

        setContent {
            ViscosityAutoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(mainVM)
                    LoadingDialog.Content()
//                    ErrorView.Content()
                }
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FilePickerManager.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                val list = FilePickerManager.obtainData()

                mainVM.firmPath.value = list[0]

            }
        }
    }


    private fun requestMyPermissions() {
        XXPermissions.with(this)
            // 申请单个权限
            .permission(Permission.WRITE_EXTERNAL_STORAGE)
            .permission(Permission.READ_EXTERNAL_STORAGE)
            .permission(Permission.REQUEST_INSTALL_PACKAGES)
            .permission(Permission.READ_PHONE_STATE)
            .permission(Permission.ACCESS_FINE_LOCATION)

            // 设置权限请求拦截器（局部设置）
            //.interceptor(new PermissionInterceptor())
            // 设置不触发错误检测机制（局部设置）
            //.unchecked()
            .request(object : OnPermissionCallback {

                override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                    if (all) {
                        return
                    }
                }

                override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                    if (never) {
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.startPermissionActivity(this@MainActivity, permissions)
                    } else {
                    }
                }
            })
    }


    override fun onDestroy() {
        super.onDestroy()
        //隐藏状态栏
        val intent = Intent("show.systemui")
        sendBroadcast(intent)
        //禁止滑动唤出状态栏
        val intent2 = Intent("com.zc.open_gesture")
       sendBroadcast(intent2)
    }


}
