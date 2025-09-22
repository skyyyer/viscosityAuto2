package com.hm.viscosityauto.ui.page

import android.app.Activity
import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asi.nav.Nav
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.page.Menu.System
import com.hm.viscosityauto.ui.page.Menu.Admin
import com.hm.viscosityauto.ui.page.Menu.AdvParam
import com.hm.viscosityauto.ui.page.Menu.Calibration
import com.hm.viscosityauto.ui.page.Menu.Clean
import com.hm.viscosityauto.ui.page.Menu.Medium
import com.hm.viscosityauto.ui.page.Menu.Param
import com.hm.viscosityauto.ui.page.Menu.Upload
import com.hm.viscosityauto.ui.page.Menu.Wlan
import com.hm.viscosityauto.ui.theme.TestCardBg
import com.hm.viscosityauto.ui.theme.textColor
import com.hm.viscosityauto.ui.theme.textColorBlue
import com.hm.viscosityauto.ui.view.AdvParamView
import com.hm.viscosityauto.ui.view.BaseButton
import com.hm.viscosityauto.ui.view.BaseDialog
import com.hm.viscosityauto.ui.view.BaseTitle
import com.hm.viscosityauto.ui.view.CalibrationView
import com.hm.viscosityauto.ui.view.CleanView
import com.hm.viscosityauto.ui.view.MediumView
import com.hm.viscosityauto.ui.view.ParamView
import com.hm.viscosityauto.ui.view.SystemView
import com.hm.viscosityauto.ui.view.UploadView
import com.hm.viscosityauto.ui.view.WlanView

import com.hm.viscosityauto.ui.view.AdminView
import com.hm.viscosityauto.utils.ToastUtil
import com.hm.viscosityauto.vm.MainVM
import com.hm.viscosityauto.vm.SettingVM
import com.hm.viscosityauto.vm.TestState
import com.iwdael.wifimanager.Wifi


object Menu {
    const val System = 0
    const val Admin = 1
    const val Wlan = 2
    const val Upload = 3
    const val Medium = 4
    const val Calibration = 5
    const val Param = 6
    const val Clean = 7
    const val AdvParam = 8
}


@Composable
fun SettingPage(vm: MainVM) {
    val settingVm: SettingVM = viewModel()

    val context = LocalContext.current

    val pwdDialogState = remember {
        mutableStateOf(false)
    }

    var selWifi = remember {
        Wifi()
    }

    val tabSel = remember {
        mutableIntStateOf(0)
    }

    DisposableEffect(Unit) {
        vm.getApp()
        onDispose { }
    }


    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp)
        ) {


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
            ) {
                BaseTitle(title = stringResource(id = R.string.setting), onBack = {
                    if (settingVm.stateA != TestState.Empty || settingVm.stateB != TestState.Empty) {
                        ToastUtil.show(context, context.getString(R.string.exit_tip))
                        return@BaseTitle
                    }
                    Nav.back()
                })
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxSize()
            ) {

                MenuView(tabSel, vm.language.value) {
                    tabSel.intValue = it
                }

                Box(modifier = Modifier.padding(vertical = 20.dp, horizontal = 40.dp)) {
                    when (tabSel.intValue) {
                        System -> {
                            SystemView(
                                vm.language.value,
                                vm.autoPrint.value,
                                vm.autoClean.value,
                                vm.autoEmpty.value,
                                "${vm.date}  ${vm.time}",
                                vm.versionName.value,
                                vm.versionCode.intValue,
                                vm.newApkPath.value,
                                onLanguage = {
                                    vm.setLanguage(it)
                                },
                                onClean = {
                                    vm.setAutoClean(it)
                                },
                                onPrint = { vm.setAutoPrint(it) },
                                onTime = { year, month, day, hour, minute, second ->
                                    vm.editTime(year, month, day, hour, minute, second)
                                }, onUpdate = {
                                    vm.installApk(context as Activity)
                                }, onEmpty = {
                                    vm.setAutoEmpty(it)
                                })
                        }

                        Admin -> {
                            AdminView(vm.adminInfo.value, vm.adminList, addAdmin = {
                                vm.addAdmin(it)
                            }, delAdmin = {
                                vm.delAdmin(it)
                            }, editAdmin = {
                                vm.editAdmin(it)
                            }, logout = {
                                vm.logout()
                            })
                        }

                        Wlan -> {
                            WlanView(
                                vm.wifiState.value,
                                vm.wifiScanState.intValue,
                                vm.wifiConnectedList,
                                vm.wifiList,
                                onStateChange = {
                                    vm.wifiState.value = it
                                    if (it) {
                                        vm.openWifi()
                                    } else {
                                        vm.closeWIFI()
                                    }
                                },
                                onConnect = {
                                    if (it.isSaved) {
                                        vm.connectWIFI(context, it)
                                    } else if (it.isEncrypt) {
                                        selWifi = it as Wifi
                                        pwdDialogState.value = true
                                    } else {
                                        vm.connectWIFI(context, it)
                                    }
//                                    vm.connectWIFI(it)

                                }, onScan = {
                                    vm.scanWIFI()
                                })
                        }

                        Upload -> {
                            UploadView(
                                vm.uploadPath.value,
                                vm.uploadUser.value,
                                vm.uploadPwd.value
                            ) { path, name, pwd ->
                                vm.editUploadInfo(path, name, pwd)
                            }
                        }

                        Medium -> {
                            MediumView(settingVm)
                        }

                        Calibration -> {
                            CalibrationView(settingVm)
                        }

                        Param -> {
                            ParamView(settingVm)
                        }

                        Clean -> {
                            CleanView(settingVm)
                        }

                        AdvParam -> {
                            AdvParamView(settingVm)
                        }
                    }
                }

            }
        }


        //wifi密码输入弹窗
        BaseDialog(dialogState = pwdDialogState) {
            WifiPwdDialogView(onCancel = {
                pwdDialogState.value = false
            }, onConfirm = {
                pwdDialogState.value = false
                vm.connectWIFI(context, selWifi, it)
            })
        }

    }

}


@Composable
fun WifiPwdDialogView(
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit
) {

    val pwd = remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .width(500.dp)
            .background(color = Color.White, shape = RoundedCornerShape(5.dp))
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.wlan_pwd),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextField(value = pwd.value, onValueChange = {
            pwd.value = it
        })


        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            BaseButton(
                title = stringResource(id = R.string.cancel),
                isNegativeStyle = true
            ) {
                onCancel()
            }
            Spacer(modifier = Modifier.width(20.dp))
            BaseButton(title = stringResource(id = R.string.confirm)) {
                onConfirm(pwd.value)
            }

        }

    }
}


@Composable
fun MenuView(tabSel: MutableIntState, language: String, onSel: (Int) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .background(TestCardBg)
            .fillMaxHeight()
            .width(160.dp)
    ) {

        Log.e("MenuView",language)

        item {
            MenuItemView(stringResource(id = R.string.system), tabSel.intValue == System) {
                onSel(System)
            }
        }
        item {
            MenuItemView(stringResource(id = R.string.admin), tabSel.intValue == Admin) {
                onSel(Admin)
            }
        }

        item {
            MenuItemView(stringResource(id = R.string.wlan), tabSel.intValue == Wlan) {
                onSel(Wlan)
            }

        }
        item {
            MenuItemView(stringResource(id = R.string.upload), tabSel.intValue == Upload) {
                onSel(Upload)
            }
        }

        item {
            MenuItemView(stringResource(id = R.string.medium), tabSel.intValue == Medium) {
                onSel(Medium)
            }
        }
        item {
            MenuItemView(stringResource(id = R.string.temperature_edit), tabSel.intValue == Calibration) {
                onSel(Calibration)
            }

        }

        item {
            MenuItemView(stringResource(id = R.string.device_param), tabSel.intValue == Param) {
                onSel(Param)
            }

        }
        item {
            MenuItemView(stringResource(id = R.string.manual_clean), tabSel.intValue == Clean) {
                onSel(Clean)
            }

        }

        item {
            MenuItemView(stringResource(id = R.string.advanced_param), tabSel.intValue == AdvParam) {
                onSel(AdvParam)
            }

        }


    }

}

@Composable
fun MenuItemView(title: String, isSel: Boolean, onSel: () -> Unit) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(80.dp)
            .background(if (isSel) Color.White else Color.Transparent)
            .clickable {
                onSel()
            }, contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(color = if (isSel) textColorBlue else textColor)
        )
    }
}



