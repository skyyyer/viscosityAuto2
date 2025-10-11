package com.hm.viscosityauto.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asi.nav.Nav
import com.google.gson.Gson
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.theme.cardBg
import com.hm.viscosityauto.ui.theme.dividerColor
import com.hm.viscosityauto.ui.theme.keyBoardBg
import com.hm.viscosityauto.ui.view.BaseButton
import com.hm.viscosityauto.ui.view.BaseTitle
import com.hm.viscosityauto.utils.SPUtils
import com.hm.viscosityauto.utils.ToastUtil
import com.hm.viscosityauto.vm.SettingVM
import com.hm.viscosityauto.vm.TestState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private const val setValueMax = 4095
private const val lightValueMax = 100


@Composable
fun ParamPage(vm: SettingVM = viewModel()) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    var extractDurA by remember {
        mutableStateOf("5")
    }
    var extractIntA by remember {
        mutableStateOf("5")
    }
    var speedA by remember {
        mutableStateOf("5")
    }

    var extractDurB by remember {
        mutableStateOf("5")
    }
    var extractIntB by remember {
        mutableStateOf("5")
    }
    var speedB by remember {
        mutableStateOf("5")
    }



    DisposableEffect(Unit) {
        scope.launch {
            vm.initDevicePort()
            delay(500)
            vm.startABValueUp(true)
        }

        onDispose {
            vm.startABValueUp(false)
            vm.closeSerialPort()
        }
    }


    //进入计时状态 执行结束
    LaunchedEffect(vm.stateA) {
        if (vm.stateA == TestState.Start) {
            vm.setState(1, 0)
        }

    }
    //进入计时状态 执行结束
    LaunchedEffect(vm.stateB) {
        if (vm.stateB == TestState.Start) {
            vm.setState(2, 0)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 28.dp)) {
            BaseTitle(title = stringResource(id = R.string.device_param), onBack = {
                if (vm.stateA != TestState.Empty || vm.stateB != TestState.Empty) {
                    ToastUtil.show(context, context.getString(R.string.exit_tip))
                    return@BaseTitle
                }
                Nav.back()
            })
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(cardBg),
            horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center, modifier = Modifier.weight(1f)

            )
            Text(
                text = stringResource(id = R.string.detected_value),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center, modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(id = R.string.set_value) + "(0-$setValueMax)",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center, modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(id = R.string.sensitivity) + "(0-$lightValueMax)",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center, modifier = Modifier.weight(1f)
            )

            Text(
                text = "",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center, modifier = Modifier.weight(1f)
            )

        }
        ItemView(
            name = "A${stringResource(id = R.string.up)}",
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            detectedValue = vm.DeviceParamModel.aUp.toString(),
            setValue = vm.DeviceParamModel.aUpSet.toString(),
            sensitivity = vm.DeviceParamModel.aUpSensitivity.toString(),
            onConfig = { setValue, sensitivity ->
                vm.DeviceParamModel = vm.DeviceParamModel.copy(
                    aUpSet = setValue.toInt(),
                    aUpSensitivity = sensitivity.toInt()
                )
                vm.setValueAndSen(1, setValue.toInt(), sensitivity.toInt())

                SPUtils.getInstance().put("deviceParam", Gson().toJson(vm.DeviceParamModel))

            }
        )

        HorizontalDivider(thickness = 1.dp, color = dividerColor)

        ItemView(
            name = "A${stringResource(id = R.string.down)}",
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            detectedValue = vm.DeviceParamModel.aDown.toString(),
            setValue = vm.DeviceParamModel.aDownSet.toString(),
            sensitivity = vm.DeviceParamModel.aDownSensitivity.toString(),
            onConfig = { setValue, sensitivity ->
                vm.DeviceParamModel = vm.DeviceParamModel.copy(
                    aDownSet = setValue.toInt(),
                    aDownSensitivity = sensitivity.toInt()
                )
                vm.setValueAndSen(2, setValue.toInt(), sensitivity.toInt())
                SPUtils.getInstance().put("deviceParam", Gson().toJson(vm.DeviceParamModel))

            }
        )
        HorizontalDivider(thickness = 1.dp, color = dividerColor)
        ItemView(
            name = "B${stringResource(id = R.string.up)}",
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            detectedValue = vm.DeviceParamModel.bUp.toString(),
            setValue = vm.DeviceParamModel.bUpSet.toString(),
            sensitivity = vm.DeviceParamModel.bUpSensitivity.toString(),
            onConfig = { setValue, sensitivity ->
                vm.DeviceParamModel = vm.DeviceParamModel.copy(
                    bUpSet = setValue.toInt(),
                    bUpSensitivity = sensitivity.toInt()
                )
                vm.setValueAndSen(3, setValue.toInt(), sensitivity.toInt())
                SPUtils.getInstance().put("deviceParam", Gson().toJson(vm.DeviceParamModel))

            }
        )
        HorizontalDivider(thickness = 1.dp, color = dividerColor)
        ItemView(
            name = "B${stringResource(id = R.string.down)}",
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            detectedValue = vm.DeviceParamModel.bDown.toString(),
            setValue = vm.DeviceParamModel.bDownSet.toString(),
            sensitivity = vm.DeviceParamModel.bDownSensitivity.toString(),
            onConfig = { setValue, sensitivity ->
                vm.DeviceParamModel = vm.DeviceParamModel.copy(
                    bDownSet = setValue.toInt(),
                    bDownSensitivity = sensitivity.toInt()
                )
                vm.setValueAndSen(4, setValue.toInt(), sensitivity.toInt())
                SPUtils.getInstance().put("deviceParam", Gson().toJson(vm.DeviceParamModel))

            }
        )
        HorizontalDivider(thickness = 1.dp, color = dividerColor)

        Spacer(modifier = Modifier.height(20.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

            ItemChannelView("A", vm.stateA, extractDurA, extractIntA, speedA,
                modifier = Modifier.width(320.dp), onEdit = { extractDur, extractInt, speed ->
                    extractDurA = extractDur
                    extractIntA = extractInt
                    speedA = speed
                }
            ) {
                if (vm.stateA == TestState.Empty) {

                    if (extractDurA.toIntOrNull() == null || extractIntA.toIntOrNull() == null || speedA.toIntOrNull() == null ||
                        extractDurB.toIntOrNull() == null || extractIntB.toIntOrNull() == null || speedB.toIntOrNull() == null
                    ) {
                        ToastUtil.show(context, context.getString(R.string.data_error))
                        return@ItemChannelView
                    }

                    scope.launch {
                        vm.setExtractParam(
                            extractDurA,
                            extractIntA,
                            speedA,
                            extractDurB,
                            extractIntB,
                            speedB
                        )
                        delay(50)
                        vm.setState(1, 1)
                    }

                } else {
                    vm.setState(1, 0)
                }

            }

            Spacer(modifier = Modifier.width(82.dp))

            ItemChannelView(
                "B", vm.stateB, extractDurB, extractIntB, speedB,
                modifier = Modifier.width(320.dp), onEdit = { extractDur, extractInt, speed ->
                    extractDurB = extractDur
                    extractIntB = extractInt
                    speedB = speed
                }
            ) {
                if (vm.stateB == TestState.Empty) {
                    if (extractDurA.toIntOrNull() == null || extractIntA.toIntOrNull() == null || speedA.toIntOrNull() == null ||
                        extractDurB.toIntOrNull() == null || extractIntB.toIntOrNull() == null || speedB.toIntOrNull() == null
                    ) {
                        ToastUtil.show(context, context.getString(R.string.data_error))
                        return@ItemChannelView
                    }
                    scope.launch {
                        vm.setExtractParam(
                            extractDurA,
                            extractIntA,
                            speedA,
                            extractDurB,
                            extractIntB,
                            speedB
                        )
                        delay(50)
                        vm.setState(2, 1)
                    }

                } else {
                    vm.setState(2, 0)
                }

            }
        }


    }
}

@Composable
private fun ItemView(
    name: String = "",
    modifier: Modifier,
    detectedValue: String,
    setValue: String,
    sensitivity: String,
    onConfig: (String, String) -> Unit
) {
    val context = LocalContext.current

    var value1Str by remember {
        mutableStateOf(setValue)
    }
    var value2Str by remember {
        mutableStateOf(sensitivity)
    }

    Row(
        modifier
            .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )


        Text(
            text = detectedValue,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)

        )

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            InputView(value = value1Str, onValueChange = {
                value1Str = it
            })
        }

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            InputView(value = value2Str, onValueChange = {
                value2Str = it
            })
        }

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            BaseButton {

                if (value1Str.toIntOrNull() == null || value2Str.toIntOrNull() == null) {
                    ToastUtil.show(context, context.getString(R.string.input_error))
                    return@BaseButton
                }


                if (value1Str.isEmpty()) {
                    value1Str = "0"
                }
                if (value2Str.isEmpty()) {
                    value2Str = "0"
                }

                if (value1Str.toInt() < 0 || value1Str.toInt() > setValueMax) {
                    ToastUtil.show(
                        context,
                        context.getString(R.string.set_value) + context.getString(R.string.over_limit)
                    )
                    return@BaseButton
                }
                if (value2Str.toInt() < 0 || value2Str.toInt() > lightValueMax) {
                    ToastUtil.show(
                        context,
                        context.getString(R.string.sensitivity) + context.getString(R.string.over_limit)
                    )
                    return@BaseButton
                }

                onConfig(value1Str, value2Str)
            }
        }

    }

}


@Composable
private fun ItemChannelView(
    name: String,
    state: Int,
    extractDur: String,
    extractInt: String,
    speed: String,
    modifier: Modifier,
    onEdit: (String, String, String) -> Unit,
    onBtn: () -> Unit,
) {

    var value1Str by remember {
        mutableStateOf(extractDur)
    }
    var value2Str by remember {
        mutableStateOf(extractInt)
    }
    var value3Str by remember {
        mutableStateOf(speed)
    }

    Column(
        modifier
            .background(cardBg, shape = RoundedCornerShape(12.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {


        Row(verticalAlignment = Alignment.Bottom) {
            Image(
                painter = painterResource(id = if (name == "A") R.mipmap.a_icon else R.mipmap.b_icon),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.weight(1f))

            BaseButton(
                stringResource(id = if (state == TestState.Empty) R.string.start else R.string.end),
                modifier = Modifier.width(80.dp)
            ) {
                onBtn()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))



        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.extract_duration) + "(s):",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            InputView(value = value1Str, enabled = (state == TestState.Empty), onValueChange = {
                value1Str = it
                onEdit(value1Str, value2Str, value3Str)
            })

        }
        Spacer(modifier = Modifier.height(10.dp))


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.extract_interval) + "(s):",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            InputView(value = value2Str, enabled = (state == TestState.Empty), onValueChange = {
                value2Str = it
                onEdit(value1Str, value2Str, value3Str)

            })
        }
        Spacer(modifier = Modifier.height(10.dp))


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.motor_speed) + "(Kpa):",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            InputView(value = value3Str, enabled = (state == TestState.Empty), onValueChange = {
                value3Str = it
                onEdit(value1Str, value2Str, value3Str)

            })
        }


    }

}