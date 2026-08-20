package com.hm.viscosityauto.ui.page

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asi.nav.Nav
import com.google.gson.Gson
import com.hm.viscosityauto.DebugPageRoute
import com.hm.viscosityauto.MyApp
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.theme.cardBgWhite
import com.hm.viscosityauto.ui.theme.textColorBlue
import com.hm.viscosityauto.ui.theme.underLine
import com.hm.viscosityauto.ui.view.BaseButton
import com.hm.viscosityauto.ui.view.BaseDialog
import com.hm.viscosityauto.ui.view.BaseTitle
import com.hm.viscosityauto.ui.view.ItemInputViewH
import com.hm.viscosityauto.utils.SPUtils
import com.hm.viscosityauto.vm.MainVM
import com.hm.viscosityauto.vm.SettingVM
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.rosuh.filepicker.config.FilePickerManager
import java.lang.reflect.Method




@SuppressLint("HardwareIds", "PrivateApi")
@Composable
fun ManagerPage(mainVM: MainVM, vm:SettingVM = viewModel()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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
    val modelDialog = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit){
        Log.e("ManagerPage","ManagerPage")
        vm.addListener()
        vm.getPumpMotorVer()
    }


    Box {
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
            Spacer(modifier = Modifier.height(8.dp))


            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(text = "设备型号: ${mainVM.deviceModel.value}")

                BaseButton(title = "修改") {
                    modelDialog.value = true
                }
            }


            Spacer(modifier = Modifier.height(8.dp))


            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(text = "使用新泵")

              Switch(checked = vm.pumpMotro.intValue==1, onCheckedChange = {
                  vm.pumpMotro.intValue = if(it) 1 else 0
                  vm.setPumpMotorVer(it)
              })
            }


            Spacer(modifier = Modifier.height(8.dp))

            BaseButton(title = "保存当前多点校准数据为默认数据") {

                vm.pointTListDef = vm.pointTList
                SPUtils.getInstance().put("pointTDef", Gson().toJson(vm.pointTListDef.toList()))
                Toast.makeText(context, context.getString(R.string.success), Toast.LENGTH_SHORT)
                    .show()
            }

            Spacer(modifier = Modifier.height(40.dp))

            BaseButton(title = stringResource(id = R.string.debug_mode)) {
                Nav.to(DebugPageRoute.route)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.weight(1f))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                BaseButton(title = "退出应用", isError = true) {
                    (context as Activity).finish()
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        BaseDialog(dialogState = modelDialog) {
            ModelDialogView(mainVM.deviceModel.value, onCancel = {
                modelDialog.value = false
            }, onConfirm = {
                modelDialog.value = false

                mainVM.deviceModel.value = it;
                SPUtils.getInstance().put("deviceModel", it)
            })
        }
    }

}



@Composable
fun ModelDialogView(
    model: String,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit
) {

    var model by remember {
        mutableStateOf(model)
    }

    Box(
        modifier = Modifier
            .width(486.dp)
            .shadow(
                elevation = 16.dp, shape = RoundedCornerShape(10.dp),
            )
            .background(color = cardBgWhite)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.device_model),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(
                color = Color.Gray.copy(alpha = 0.5f),
                thickness = 1.dp,
            )
            Spacer(modifier = Modifier.height(40.dp))



            Box(
                modifier = Modifier
                    .drawBehind {
                        val strokeWidth = 2.dp.toPx()
                        // 绘制下划线（带 16dp 左右边距）
                        drawLine(
                            color = underLine.copy(alpha = 0.5f),
                            start = Offset(16.dp.toPx(), size.height - strokeWidth / 2),
                            end = Offset(size.width - 16.dp.toPx(), size.height - strokeWidth / 2),
                            strokeWidth = strokeWidth
                        )
                    }
                    .padding(4.dp)
            ) {
                BasicTextField(
                    value = model,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                    singleLine = true,
                    modifier = Modifier
                        .size(388.dp, 42.dp)
                        .background(color = Color.Transparent)
                        .padding(end = 36.dp),
                    onValueChange = {
                        model = it
                    })
            }

            Spacer(modifier = Modifier.height(40.dp))

            HorizontalDivider(
                color = Color.Gray.copy(alpha = 0.5f),
                thickness = 1.dp,
            )

            Row(modifier = Modifier.height(54.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {
                            onCancel()
                        }, contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(id = R.string.cancel),
                        style = MaterialTheme.typography.titleSmall.copy(color = textColorBlue),
                        textAlign = TextAlign.Center
                    )
                }

                VerticalDivider(
                    modifier = Modifier
                        .width(2.dp)
                        .height(30.dp),
                    color = Color.Gray.copy(alpha = 0.5f),
                    thickness = 1.dp,
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {
                            onConfirm(model)
                        }, contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(id = R.string.confirm),
                        style = MaterialTheme.typography.titleSmall.copy(color = textColorBlue),
                        textAlign = TextAlign.Center
                    )
                }
            }

        }

    }

}
