package com.hm.viscosityauto.ui.page

import NoPressStateClick
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asi.nav.Nav
import com.google.gson.Gson
import com.hm.viscosity.model.MediumModel
import com.hm.viscosityauto.AdminPageRoute
import com.hm.viscosityauto.AvdParamPageRoute
import com.hm.viscosityauto.CleanPageRoute
import com.hm.viscosityauto.DeviceParamPageRoute
import com.hm.viscosityauto.ManagerPageRoute
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.theme.cardBg
import com.hm.viscosityauto.ui.theme.cardBgWhite
import com.hm.viscosityauto.ui.theme.dividerColor
import com.hm.viscosityauto.ui.theme.textColor
import com.hm.viscosityauto.ui.theme.textColorBlue
import com.hm.viscosityauto.ui.theme.textColorGray
import com.hm.viscosityauto.ui.view.AddMediumView
import com.hm.viscosityauto.ui.view.BaseButton
import com.hm.viscosityauto.ui.view.BaseDialog
import com.hm.viscosityauto.ui.view.BaseTitle
import com.hm.viscosityauto.ui.view.CalibrationMulView
import com.hm.viscosityauto.ui.view.CalibrationSingleView
import com.hm.viscosityauto.ui.view.WlanView

import com.hm.viscosityauto.ui.view.CustomWidthSwitch
import com.hm.viscosityauto.ui.view.ItemLab
import com.hm.viscosityauto.ui.view.PwdDialogView
import com.hm.viscosityauto.ui.view.TimerPickerView
import com.hm.viscosityauto.ui.view.click.doubleClick
import com.hm.viscosityauto.utils.FileUtil
import com.hm.viscosityauto.utils.SPUtils
import com.hm.viscosityauto.utils.ToastUtil
import com.hm.viscosityauto.vm.CalibrationState
import com.hm.viscosityauto.vm.LANGUAGE_EN
import com.hm.viscosityauto.vm.LANGUAGE_ZH
import com.hm.viscosityauto.vm.MainVM
import com.hm.viscosityauto.vm.SettingVM
import com.hm.viscosityauto.vm.TestState
import com.iwdael.wifimanager.Wifi
import java.io.File


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingPage(vm: MainVM) {
    val settingVm: SettingVM = viewModel()

    val context = LocalContext.current

    val pwdDialogState = remember {
        mutableStateOf(false)
    }
    val wifiDialogState = remember {
        mutableStateOf(false)
    }

    val timePickerDialog = remember {
        mutableStateOf(false)
    }

    val managerDialog = remember {
        mutableStateOf(false)
    }

    var selWifi = remember {
        Wifi()
    }


    val addMediumDialog = remember {
        mutableStateOf(false)
    }


    val delMediumDialog = remember {
        mutableStateOf(false)
    }
    var selMediumIndex by remember {
        mutableIntStateOf(0)
    }


    //单点校准
    val calibrationSingleDialog = remember {
        mutableStateOf(false)
    }

    //多点校准
    val calibrationMulDialog = remember {
        mutableStateOf(false)
    }


    DisposableEffect(Unit) {
        vm.getApp()
        onDispose { }
    }


    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp, bottom = 64.dp)
        ) {


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
            ) {
                BaseTitle(title = stringResource(id = R.string.setting), onBack = {
                    Nav.back()
                })

                Row(
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 126.dp)
                ) {

                    BaseButton(
                        title = stringResource(id = R.string.wifi),
                        icon = R.mipmap.wifi_icon
                    ) {
                        wifiDialogState.value = true
                    }

                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(modifier = Modifier
                .weight(1f)
                .padding(horizontal = 40.dp), content = {
                item {
                    //系统设置
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.mipmap.system_icon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(id = R.string.system),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 30.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.language),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.width(120.dp)
                        )

                        Row(
                            modifier = Modifier.NoPressStateClick(onClick = {
                                vm.setLanguage(LANGUAGE_ZH)
                            }),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(id = if (vm.language.value == LANGUAGE_ZH) R.mipmap.selected_icon2 else R.mipmap.select_icon2),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(id = R.string.chinese),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Spacer(modifier = Modifier.width(80.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.NoPressStateClick(onClick = {

                                vm.setLanguage(LANGUAGE_EN)
                            })
                        ) {
                            Image(
                                painter = painterResource(id = if (vm.language.value == LANGUAGE_EN) R.mipmap.selected_icon2 else R.mipmap.select_icon2),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(id = R.string.english),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Spacer(modifier = Modifier.width(200.dp))


                        Text(
                            text = stringResource(id = R.string.time),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.width(120.dp)
                        )

                        Box(
                            modifier = Modifier
                                .clickable {
                                    timePickerDialog.value = true
                                }, contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${vm.date}  ${vm.time}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 30.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.version),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.width(120.dp)
                        )


                        Text(
                            text = "VERSION ${vm.versionName.value}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.doubleClick {
                                managerDialog.value = true
                            }
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        if (vm.newApkPath.value.isNotEmpty()) {
                            if (File(vm.newApkPath.value).exists() && FileUtil.extractVersionCodeFromApk(
                                    context,
                                    File(vm.newApkPath.value)
                                ) > vm.versionCode.intValue
                            ) {
                                BaseButton(
                                    isBrush = false,
                                    title = stringResource(id = R.string.update)
                                ) {
                                    vm.installApk(context as Activity)
                                }
                            }
                        }

                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = dividerColor
                    )
                    Spacer(modifier = Modifier.height(28.dp))


                    //温度设置
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.mipmap.temp_setting_icon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(id = R.string.temperature_edit),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 30.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.temperature_edit_1),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .width(120.dp)
                                .clickable {
                                    calibrationSingleDialog.value = true
                                }
                        )

                        CustomWidthSwitch(
                            trackWidth = 52.dp,
                            trackHeight = 24.dp,
                            thumbSize = 20.dp,
                            checked = settingVm.calibrationState.intValue == CalibrationState.Single,
                            onCheckedChange = {
                                if (it) {
                                    settingVm.setCalibrationState(CalibrationState.Single)
                                } else {
                                    settingVm.setCalibrationState(CalibrationState.None)
                                }
                            })

                        Spacer(modifier = Modifier.width(200.dp))

                        Text(
                            text = stringResource(id = R.string.temperature_edit_3),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .width(120.dp)
                                .clickable {
                                    calibrationMulDialog.value = true
                                }
                        )

                        CustomWidthSwitch(
                            trackWidth = 52.dp,
                            trackHeight = 24.dp,
                            thumbSize = 20.dp,
                            checked = settingVm.calibrationState.intValue == CalibrationState.Mul,
                            onCheckedChange = {
                                if (it) {
                                    settingVm.setCalibrationState(CalibrationState.Mul)
                                } else {
                                    settingVm.setCalibrationState(CalibrationState.None)
                                }
                            })
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = dividerColor
                    )
                    Spacer(modifier = Modifier.height(28.dp))

                    //介质设置
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.mipmap.media_icon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(id = R.string.medium),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        BaseButton(stringResource(id = R.string.medium_add)) {
                            addMediumDialog.value = true
                        }

                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(8.dp)
                            .background(Color.Transparent),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        settingVm.mediumList.forEachIndexed { index, model ->

                            ItemLab(
                                title = model.name,
                                isSle = model.isSel,
                                isCanDel = model.isCanDel,
                                onClick = {
                                    settingVm.mediumList.forEach {
                                        it.isSel = false
                                    }
                                    settingVm.mediumList[index] =
                                        settingVm.mediumList[index].copy(isSel = true)

                                    settingVm.setMedium(settingVm.mediumList[index].p.toInt())
                                },
                                onLongClick = {
                                    if (model.isCanDel) {
                                        selMediumIndex = index
                                        delMediumDialog.value = true
                                    } else {
                                        Toast.makeText(
                                            context,
                                            context.getText(R.string.cant_del_tip),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                        }
                    }

                }
            })

        }

        //底部菜单
        Row(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .background(color = cardBg)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable {
                        Nav.to(AdminPageRoute.route)
                    }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.admin_manager),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            VerticalDivider(
                thickness = 1.dp, color = dividerColor, modifier = Modifier.height(40.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable {
                        Nav.to(AvdParamPageRoute.route)
                    }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.advanced_param),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            VerticalDivider(
                thickness = 1.dp, color = dividerColor, modifier = Modifier.height(40.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable {
                        Nav.to(DeviceParamPageRoute.route)
                    }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.device_param),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            VerticalDivider(
                thickness = 1.dp, color = dividerColor, modifier = Modifier.height(40.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable {
                        Nav.to(CleanPageRoute.route)
                    }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.manual_clean),
                    style = MaterialTheme.typography.titleSmall,
                )
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


        //添加介质
        if (addMediumDialog.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.1f))
                    .NoPressStateClick(onClick = {

                    }),
                contentAlignment = Alignment.Center
            ) {
                AddMediumView(
                    settingVm.curTemperature,
                    settingVm.heatingState,
                    onConfirm = { name, p ->
                        if (name.isEmpty() || p.isEmpty()) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.input_completely),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            addMediumDialog.value = false

                            settingVm.mediumList.add(
                                MediumModel(
                                    p,
                                    name,
                                    isSel = false,
                                    isCanDel = true
                                )
                            )
                            SPUtils.getInstance()
                                .put("mediumInfo", Gson().toJson(settingVm.mediumList))
                        }

                    },
                    onDebug = {
                        settingVm.setMedium(it.toInt())
                    },
                    onCancel = {
                        addMediumDialog.value = false
                        val index = settingVm.mediumList.indexOfFirst {
                            it.isSel
                        }
                        settingVm.setMedium(settingVm.mediumList[index].p.toInt())

                    },
                    setT = {
                        settingVm.setTemperature(it)
                    },
                    stopTemperature = {
                        settingVm.stopTemperature()
                    })
            }

        }


        //删除介质弹框
        BaseDialog(contentView = {
            Column(
                modifier = Modifier
                    .width(440.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(5.dp))
                    .padding(30.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(id = R.string.tip),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(id = R.string.del_tip),
                    style = MaterialTheme.typography.bodyLarge.copy(color = textColorGray)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = settingVm.mediumList[selMediumIndex].name + "(${settingVm.mediumList[selMediumIndex].p})",
                    style = MaterialTheme.typography.bodyLarge.copy(color = textColorGray)
                )

                Spacer(modifier = Modifier.height(26.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    BaseButton(
                        title = stringResource(id = R.string.cancel),
                        isNegativeStyle = true
                    ) {
                        delMediumDialog.value = false
                    }
                    Spacer(modifier = Modifier.width(16.dp))

                    BaseButton(title = stringResource(id = R.string.confirm)) {
                        delMediumDialog.value = false

                        if (settingVm.mediumList[selMediumIndex].isSel) {
                            settingVm.mediumList[0] = settingVm.mediumList[0].copy(isSel = true)
                        }
                        settingVm.mediumList.removeAt(selMediumIndex)


                        SPUtils.getInstance()
                            .put("mediumInfo", Gson().toJson(settingVm.mediumList))
                    }

                }

            }

        }, dialogState = delMediumDialog)


        //时间选择
        if (timePickerDialog.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.1f))
                    .NoPressStateClick(onClick = {

                    }),
                contentAlignment = Alignment.Center
            ) {
                TimerPickerView(onConfirm = { year, month, day, hour, minute, second ->
                    vm.editTime(year, month, day, hour, minute, second)
                    timePickerDialog.value = false
                }) {
                    timePickerDialog.value = false
                }
            }

        }

        //管理员弹窗

        BaseDialog(dialogState = managerDialog) {
            PwdDialogView(stringResource(id = R.string.manager_pwd), onCancel = {
                managerDialog.value = false
            }, onConfirm = {
                managerDialog.value = false
                if (it == "0523") {
                    Nav.to(ManagerPageRoute.route)
                }
            })
        }


        //wifi弹窗
        if (wifiDialogState.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.1f))
                    .NoPressStateClick(onClick = {

                    }),
                contentAlignment = Alignment.Center
            ) {
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
                    }, onClose = {
                        wifiDialogState.value = false
                    })
            }
        }


        //单点弹窗
        if (calibrationSingleDialog.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.1f))
                    .NoPressStateClick(onClick = {

                    }),
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(600.dp, 320.dp)
                        .shadow(
                            elevation = 16.dp, shape = RoundedCornerShape(10.dp),
                        )
                        .background(color = cardBgWhite)
                ) {

                    Image(
                        painter = painterResource(id = R.mipmap.close_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 6.dp, top = 6.dp)
                            .size(26.dp)
                            .align(Alignment.TopEnd)
                            .clip(shape = RoundedCornerShape(13.dp))
                            .clickable {
                                calibrationSingleDialog.value = false
                            }
                    )

                    CalibrationSingleView(settingVm)


                }
            }
        }


        //多点弹窗
        if (calibrationMulDialog.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.1f))
                    .NoPressStateClick(onClick = {

                    }),
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .width(600.dp)
                        .shadow(
                            elevation = 16.dp, shape = RoundedCornerShape(10.dp),
                        )
                        .background(color = cardBgWhite)
                ) {

                    Image(
                        painter = painterResource(id = R.mipmap.close_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 6.dp, top = 6.dp)
                            .size(26.dp)
                            .align(Alignment.TopEnd)
                            .clip(shape = RoundedCornerShape(13.dp))
                            .clickable {
                                calibrationMulDialog.value = false
                            }
                    )

                    CalibrationMulView(settingVm)

                }
            }


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
            style = MaterialTheme.typography.titleMedium.copy(textColorBlue)
        )

        Spacer(modifier = Modifier.height(32.dp))


        InputView(value = pwd.value, width = 300.dp, height = 50.dp, onValueChange = {  pwd.value = it})

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


