package com.hm.viscosityauto.ui.page

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asi.nav.Nav
import com.google.gson.Gson
import com.hm.viscosityauto.DebugPageRoute
import com.hm.viscosityauto.MyApp
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.view.BaseButton
import com.hm.viscosityauto.ui.view.BaseTitle
import com.hm.viscosityauto.utils.SPUtils
import com.hm.viscosityauto.vm.MainVM
import com.hm.viscosityauto.vm.SettingVM
import com.hm.viscosityauto.vm.TestVM
import java.lang.reflect.Method

@SuppressLint("HardwareIds", "PrivateApi")
@Composable
fun ManagerPage(vm:SettingVM = viewModel()) {
    val context = LocalContext.current

    val devId = remember {
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
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(vertical = 28.dp, horizontal = 32.dp)
    ) {
        //标题
        BaseTitle(title = "管理", onBack = {
            Nav.back()
        })


        Spacer(modifier = Modifier.height(40.dp))

        Text(text = "设备ID: $devId")


        Spacer(modifier = Modifier.height(40.dp))

        BaseButton(title = "保存当前多点校准数据为默认数据") {

            vm.pointTListDef = vm.pointTList
            SPUtils.getInstance().put("pointTDef", Gson().toJson(vm.pointTListDef.toList()))
            Toast.makeText(context,context.getString(R.string.success), Toast.LENGTH_SHORT).show()
        }

        Spacer(modifier = Modifier.height(40.dp))

        BaseButton(title = stringResource(id = R.string.debug_mode)) {
            Nav.to(DebugPageRoute.route)
        }

        Spacer(modifier = Modifier.weight(1f))

        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
            BaseButton(title = "退出应用", isError = true) {
                (context as Activity).finish()
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

}
