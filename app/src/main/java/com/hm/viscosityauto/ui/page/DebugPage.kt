package com.hm.viscosityauto.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asi.nav.Nav
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.theme.cardBgBlue
import com.hm.viscosityauto.ui.theme.cardBgGray
import com.hm.viscosityauto.ui.theme.keyBoardBg
import com.hm.viscosityauto.ui.view.BaseButton
import com.hm.viscosityauto.ui.view.BaseTitle
import com.hm.viscosityauto.utils.ToastUtil
import com.hm.viscosityauto.vm.CalibrationState
import com.hm.viscosityauto.vm.SettingVM
import com.hm.viscosityauto.vm.TestVM

@Composable
fun DebugPage(vm: SettingVM = viewModel()) {

    val context = LocalContext.current

    //  测试模式
    var debugMode by remember {
        mutableStateOf(
            false
        )
    }
    //  电机测试
    var motorTest by remember {
        mutableStateOf(
            false
        )
    }

    //  电机 速度
    var motorSpeed by remember {
        mutableIntStateOf(
            0
        )
    }

    //  电机方向
    var motorDirection by remember {
        mutableIntStateOf(
            0
        )
    }


    var state1 by remember {
        mutableStateOf(
            false
        )
    }
    var state2 by remember {
        mutableStateOf(
            false
        )
    }

    var state3 by remember {
        mutableStateOf(
            false
        )
    }

    var state4 by remember {
        mutableStateOf(
            false
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(vertical = 28.dp, horizontal = 32.dp)
    ) {
        BaseTitle(stringResource(id = R.string.debug_mode), onBack = {
            debugMode = false
            vm.debugMode(false)
            Nav.back()
        })
        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Text(
                text = stringResource(id = R.string.debug_mode),
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.weight(1f))

            Switch(modifier = Modifier
                .height(20.dp),
                checked = debugMode,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = cardBgBlue,
                    uncheckedThumbColor = Color.White,
                    uncheckedBorderColor = cardBgGray,
                    uncheckedTrackColor = cardBgGray
                ),
                onCheckedChange = {
                    debugMode = it
                    vm.debugMode(it)
                })

        }

        Spacer(modifier = Modifier.height(20.dp))

        if (debugMode) {
            Text(
                text = stringResource(id = R.string.solenoid_valve_test),
                style = MaterialTheme.typography.titleSmall
            )

            Row {
                Column {
                    Text(text = "1", modifier = Modifier.width(150.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Switch(checked = state1, colors = SwitchDefaults.colors(
                        checkedTrackColor = cardBgBlue,
                        uncheckedThumbColor = Color.White,
                        uncheckedBorderColor = cardBgGray,
                        uncheckedTrackColor = cardBgGray
                    ), onCheckedChange = {
                        state1 = it
                        vm.solenoidValveSetting(state1, state2, state3, state4)
                    })

                }
                Column {
                    Text(text = "2", modifier = Modifier.width(150.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Switch(checked = state2, colors = SwitchDefaults.colors(
                        checkedTrackColor = cardBgBlue,
                        uncheckedThumbColor = Color.White,
                        uncheckedBorderColor = cardBgGray,
                        uncheckedTrackColor = cardBgGray
                    ), onCheckedChange = {
                        state2 = it
                        vm.solenoidValveSetting(state1, state2, state3, state4)
                    })

                }
                Column {
                    Text(text = "3", modifier = Modifier.width(150.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Switch(checked = state3, colors = SwitchDefaults.colors(
                        checkedTrackColor = cardBgBlue,
                        uncheckedThumbColor = Color.White,
                        uncheckedBorderColor = cardBgGray,
                        uncheckedTrackColor = cardBgGray
                    ), onCheckedChange = {
                        state3 = it
                        vm.solenoidValveSetting(state1, state2, state3, state4)
                    })

                }
                Column {
                    Text(text = "4", modifier = Modifier.width(150.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Switch(checked = state4, colors = SwitchDefaults.colors(
                        checkedTrackColor = cardBgBlue,
                        uncheckedThumbColor = Color.White,
                        uncheckedBorderColor = cardBgGray,
                        uncheckedTrackColor = cardBgGray
                    ), onCheckedChange = {
                        state4 = it
                        vm.solenoidValveSetting(state1, state2, state3, state4)
                    })

                }
            }

            Spacer(modifier = Modifier.height(20.dp))


            Row {
                Text(
                    text = stringResource(id = R.string.motor_test),
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.weight(1f))

                Switch(modifier = Modifier
                    .height(20.dp),
                    checked = motorTest,
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = cardBgBlue,
                        uncheckedThumbColor = Color.White,
                        uncheckedBorderColor = cardBgGray,
                        uncheckedTrackColor = cardBgGray
                    ),
                    onCheckedChange = {
                        motorTest = it
                        vm.motorSetting(motorTest, motorDirection, motorSpeed)
                    })

            }
            if (motorTest) {
                Spacer(modifier = Modifier.height(20.dp))
                Row {
                    Text(
                        text = stringResource(id = R.string.speed)+"(0-100)",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    BasicTextField(
                        value = motorSpeed.toString(),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .size(100.dp, 32.dp)
                            .background(color = keyBoardBg)
                            .wrapContentSize(Alignment.Center)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        onValueChange = {
                            motorSpeed = it.toInt()
                        })

                    Spacer(modifier = Modifier.width(16.dp))

                    BaseButton {
                        if (motorSpeed<0||motorSpeed>100){
                            ToastUtil.show(context,context.getString(R.string.over_limit))
                            return@BaseButton
                        }

                        vm.motorSetting(motorTest, motorDirection, motorSpeed)
                    }

                }
                Spacer(modifier = Modifier.height(20.dp))
                Row {

                    Text(
                        text = stringResource(id = R.string.direction),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    RadioButton(selected = motorDirection == 0, onClick = {
                        motorDirection = 0
                        vm.motorSetting(motorTest, motorDirection, motorSpeed)
                    })
                    Text(
                        text = stringResource(id = R.string.forward),
                        style = MaterialTheme.typography.titleSmall
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    RadioButton(selected = motorDirection == 1, onClick = {
                        motorDirection = 1
                        vm.motorSetting(motorTest, motorDirection, motorSpeed)
                    })
                    Text(
                        text = stringResource(id = R.string.reverse),
                        style = MaterialTheme.typography.titleSmall
                    )
                }

            }

        }


    }
}