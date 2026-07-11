package com.hm.viscosityauto.ui.page

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asi.nav.Nav
import com.google.gson.Gson
import com.hm.viscosityauto.R
import com.hm.viscosityauto.model.DeviceParamModel
import com.hm.viscosityauto.ui.theme.cardBg
import com.hm.viscosityauto.ui.theme.cardBgWhite
import com.hm.viscosityauto.ui.theme.dividerColor
import com.hm.viscosityauto.ui.theme.inputBgWhite
import com.hm.viscosityauto.ui.theme.textColorBlue
import com.hm.viscosityauto.ui.view.BaseButton
import com.hm.viscosityauto.ui.view.BaseTitle
import com.hm.viscosityauto.utils.SPUtils
import com.hm.viscosityauto.utils.ToastUtil
import com.hm.viscosityauto.vm.SettingVM
import com.hm.viscosityauto.vm.TestState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


const val setValueMax = 4095
const val setValueMin = 10
const val lightValueMax = 100
const val lightValueMin = 0

@Composable
fun ParamPage(vm: SettingVM = viewModel()) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    var extractDurA by remember {
        mutableStateOf(vm.ExtractModelA.extracttime)
    }
    var extractIntA by remember {
        mutableStateOf(vm.ExtractModelA.extracttimejg)
    }
    var speedA by remember {
        mutableStateOf(vm.ExtractModelA.motorspeed)
    }

    var extractDurB by remember {
        mutableStateOf(vm.ExtractModelB.extracttime)
    }
    var extractIntB by remember {
        mutableStateOf(vm.ExtractModelB.extracttimejg)
    }
    var speedB by remember {
        mutableStateOf(vm.ExtractModelB.motorspeed)
    }


    val configDialog = remember {
        mutableStateOf(false)
    }


    DisposableEffect(Unit) {
        scope.launch {
            vm.initDevicePort()
            delay(500)
            vm.startABValueUp(true)
            delay(500)
            vm.getSensorLight()
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
        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
            BaseTitle(title = stringResource(id = R.string.device_param), onBack = {
                if (configDialog.value) {
                    configDialog.value = false
                    return@BaseTitle
                }

                if (vm.stateA != TestState.Empty || vm.stateB != TestState.Empty) {
                    ToastUtil.show(context, context.getString(R.string.exit_tip))
                    return@BaseTitle
                }
                Nav.back()
            })

            if (!configDialog.value) {
                BaseButton(
                    title = stringResource(id = R.string.config), icon = R.mipmap.device_param_icon,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 126.dp)
                ) {
                    configDialog.value = true
                }
            }

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
                text = stringResource(id = R.string.set_value) + "($setValueMin-$setValueMax)",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center, modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(id = R.string.sensitivity) + "($lightValueMin-$lightValueMax)",
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
            detectedValue = vm.DeviceParamModel.aUp,
            setValue = vm.DeviceParamModel.aUpSet,
            sensitivity = vm.DeviceParamModel.aUpSensitivity,
            onValueChange1 = {
                vm.DeviceParamModel = vm.DeviceParamModel.copy(aUpSet = it)
            },
            onValueChange2 = {
                vm.DeviceParamModel = vm.DeviceParamModel.copy(aUpSensitivity = it)
            },
            onConfig = { setValue, sensitivity ->
                vm.setValueAndSen(1, setValue.toInt(), sensitivity.toInt())

                SPUtils.getInstance().put("deviceParamInfo", Gson().toJson(vm.DeviceParamModel))

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
            onValueChange1 = {
                vm.DeviceParamModel = vm.DeviceParamModel.copy(aDownSet = it)
            },
            onValueChange2 = {
                vm.DeviceParamModel = vm.DeviceParamModel.copy(aDownSensitivity = it)
            },
            onConfig = { setValue, sensitivity ->
                vm.setValueAndSen(2, setValue.toInt(), sensitivity.toInt())
                SPUtils.getInstance().put("deviceParamInfo", Gson().toJson(vm.DeviceParamModel))

            }
        )
        HorizontalDivider(thickness = 1.dp, color = dividerColor)
        ItemView(
            name = "B${stringResource(id = R.string.up)}",
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            detectedValue = vm.DeviceParamModel.bUp,
            setValue = vm.DeviceParamModel.bUpSet,
            sensitivity = vm.DeviceParamModel.bUpSensitivity,
            onValueChange1 = {
                vm.DeviceParamModel = vm.DeviceParamModel.copy(bUpSet = it)
            },
            onValueChange2 = {
                vm.DeviceParamModel = vm.DeviceParamModel.copy(bUpSensitivity = it)
            },
            onConfig = { setValue, sensitivity ->
                vm.setValueAndSen(3, setValue.toInt(), sensitivity.toInt())
                SPUtils.getInstance().put("deviceParamInfo", Gson().toJson(vm.DeviceParamModel))

            }
        )
        HorizontalDivider(thickness = 1.dp, color = dividerColor)
        ItemView(
            name = "B${stringResource(id = R.string.down)}",
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            detectedValue = vm.DeviceParamModel.bDown,
            setValue = vm.DeviceParamModel.bDownSet,
            sensitivity = vm.DeviceParamModel.bDownSensitivity,
            onValueChange1 = {
                vm.DeviceParamModel = vm.DeviceParamModel.copy(bDownSet = it)
            },
            onValueChange2 = {
                vm.DeviceParamModel = vm.DeviceParamModel.copy(bDownSensitivity = it)
            },
            onConfig = { setValue, sensitivity ->
                vm.setValueAndSen(4, setValue.toInt(), sensitivity.toInt())
                SPUtils.getInstance().put("deviceParamInfo", Gson().toJson(vm.DeviceParamModel))

            }
        )
        HorizontalDivider(thickness = 1.dp, color = dividerColor)

        Spacer(modifier = Modifier.height(20.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

            ItemChannelView("A", vm.stateA, extractDurA, extractIntA, speedA,
                modifier = Modifier.width(424.dp), onEdit = { extractDur, extractInt, speed ->
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
                        vm.setState(1, 6)
                    }
                    vm.ExtractModelA = vm.ExtractModelA.copy(extracttime = extractDurA, extracttimejg = extractIntA, motorspeed = speedA)

                    SPUtils.getInstance().put("extractModelA",Gson().toJson(vm.ExtractModelA))


                } else {
                    vm.setState(1, 0)
                }

            }

            Spacer(modifier = Modifier.width(56.dp))

            ItemChannelView(
                "B", vm.stateB, extractDurB, extractIntB, speedB,
                modifier = Modifier.width(424.dp), onEdit = { extractDur, extractInt, speed ->
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
                        vm.setState(2, 6)
                    }
                    vm.ExtractModelB = vm.ExtractModelB.copy(extracttime = extractDurB, extracttimejg = extractIntB, motorspeed = speedB)
                    SPUtils.getInstance().put("extractModelB",Gson().toJson(vm.ExtractModelB))

                } else {
                    vm.setState(2, 0)
                }

            }
        }


    }


    //配置列表
    if (configDialog.value) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 82.dp, start = 20.dp, end = 20.dp, bottom = 10.dp)
                .background(color = cardBgWhite)
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LazyRow(content = {
                itemsIndexed(vm.deviceParamModelConfigList, key = { index, item ->
                    index
                }) { index: Int, item: DeviceParamModel ->
                    ConfigItemView(index, item, vm.DeviceParamModel, onSel = {
                        vm.DeviceParamModel = it
                        SPUtils.getInstance().put(
                            "deviceParamInfo",
                            Gson().toJson(vm.DeviceParamModel)
                        )
                        configDialog.value = false

                        scope.launch {
                            vm.setValueAndSen(
                                1,
                                vm.DeviceParamModel.aUpSet.toInt(),
                                vm.DeviceParamModel.aUpSensitivity.toInt()
                            )
                            delay(100)
                            vm.setValueAndSen(
                                2,
                                vm.DeviceParamModel.aDownSet.toInt(),
                                vm.DeviceParamModel.aDownSensitivity.toInt()
                            )
                            delay(100)
                            vm.setValueAndSen(
                                3,
                                vm.DeviceParamModel.bUpSet.toInt(),
                                vm.DeviceParamModel.bUpSensitivity.toInt()
                            )
                            delay(100)
                            vm.setValueAndSen(
                                4,
                                vm.DeviceParamModel.bDownSet.toInt(),
                                vm.DeviceParamModel.bDownSensitivity.toInt()
                            )
                        }


                    }, onSave = {
                        vm.deviceParamModelConfigList[index] = it
                        SPUtils.getInstance().put(
                            "deviceParamConfigInfo",
                            Gson().toJson(vm.deviceParamModelConfigList)
                        )
                    })

                }
            })


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
    onValueChange1: (String) -> Unit,
    onValueChange2: (String) -> Unit,
    onConfig: (String, String) -> Unit
) {
    val context = LocalContext.current


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
            InputView(value = setValue, onValueChange = {
                onValueChange1(it)
            })
        }

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            InputView(value = sensitivity, onValueChange = {
                onValueChange2(it)
            })
        }

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            BaseButton(modifier = Modifier.width(102.dp)) {

                if (setValue.toIntOrNull() == null || sensitivity.toIntOrNull() == null) {
                    ToastUtil.show(context, context.getString(R.string.input_error))
                    return@BaseButton
                }


                if (setValue.isEmpty()) {
                    onValueChange1("0")
                }
                if (sensitivity.isEmpty()) {
                    onValueChange2("0")
                }

                if (setValue.toInt() !in setValueMin..setValueMax ) {
                    ToastUtil.show(
                        context,
                        context.getString(R.string.set_value) + context.getString(R.string.over_limit)
                    )
                    return@BaseButton
                }
                if (sensitivity.toInt() < lightValueMin || sensitivity.toInt() > lightValueMax) {
                    ToastUtil.show(
                        context,
                        context.getString(R.string.sensitivity) + context.getString(R.string.over_limit)
                    )
                    return@BaseButton
                }

                onConfig(setValue, sensitivity)
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
            .padding(horizontal = 75.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {


        Row(verticalAlignment = Alignment.Bottom) {
            Image(
                painter = painterResource(id = if (name == "A") R.mipmap.a_icon else R.mipmap.b_icon),
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.weight(1f))

            BaseButton(
                stringResource(id = if (state == TestState.Empty) R.string.start else R.string.end),
                style = MaterialTheme.typography.titleSmall.copy(
                    color = Color.White,
                ),
                isPaddingV = false,
                modifier = Modifier.width(83.dp),

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

            InputView(
                value = value1Str,
                width = 83.dp,
                height = 24.dp,
                enabled = (state == TestState.Empty),
                onValueChange = {
                    value1Str = it
                    onEdit(value1Str, value2Str, value3Str)
                })

        }
        Spacer(modifier = Modifier.height(8.dp))


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
            InputView(
                value = value2Str,
                width = 83.dp,
                height = 24.dp,
                enabled = (state == TestState.Empty),
                onValueChange = {
                    value2Str = it
                    onEdit(value1Str, value2Str, value3Str)

                })
        }
        Spacer(modifier = Modifier.height(8.dp))


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
            InputView(
                value = value3Str,
                width = 83.dp,
                height = 24.dp,
                enabled = (state == TestState.Empty),
                onValueChange = {
                    value3Str = it
                    onEdit(value1Str, value2Str, value3Str)

                })
        }


    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfigItemView(
    index: Int,
    model: DeviceParamModel,
    curModel: DeviceParamModel,
    onSel: (DeviceParamModel) -> Unit, onSave: (DeviceParamModel) -> Unit,
) {
    val context = LocalContext.current

    var deviceParamModel by remember {
        mutableStateOf(model)
    }
    var name by remember {
        mutableStateOf(model.name)
    }
    var isEdit by remember {
        mutableStateOf(false)
    }


    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .width(304.dp)
            .fillMaxHeight()
            .background(
                color = cardBg,
                shape = RoundedCornerShape(5.dp)
            )
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        if (isEdit) {


            BasicTextField(
                value = deviceParamModel.name.ifEmpty { "" },
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    color = textColorBlue,
                    textAlign = TextAlign.Center
                ),
                singleLine = true,
                modifier = Modifier
                    .wrapContentWidth()
                    .width(160.dp)
                    .background(color = Color.Transparent)
                    .wrapContentSize(Alignment.Center),
                onValueChange = {
                    deviceParamModel = deviceParamModel.copy(name = it)
                }, decorationBox = { innerTextField ->

                    Box(contentAlignment = Alignment.BottomCenter) {
                        innerTextField()//自定义样式这行代码是关键，没有这一行输入文字后无法展示，光标也看不到
                        HorizontalDivider(
                            thickness = 1.dp, color = textColorBlue, modifier = Modifier.align(
                                Alignment.BottomCenter
                            )
                        )
                    }
                })

        } else {
            Text(
                text = deviceParamModel.name.ifEmpty { stringResource(id = R.string.config) + (index + 1) },
                style = MaterialTheme.typography.titleMedium.copy(textColorBlue), maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        if (deviceParamModel.name.isNotEmpty() || isEdit) {

            Text(
                text = stringResource(id = R.string.set_value) + "($setValueMin-$setValueMax)",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center, modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.height(12.dp))

            ItemInputData(
                name = "A${stringResource(id = R.string.up)}",
                value = deviceParamModel.aUpSet,
                isEdit = isEdit,
                overflow = TextOverflow.Visible,
                onInput = {
                    deviceParamModel = deviceParamModel.copy(aUpSet = it)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            ItemInputData(
                name = "A${stringResource(id = R.string.down)}",
                value = deviceParamModel.aDownSet,
                isEdit = isEdit,
                onInput = {
                    deviceParamModel = deviceParamModel.copy(aDownSet = it)

                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ItemInputData(
                name = "B${stringResource(id = R.string.up)}",
                value = deviceParamModel.bUpSet,
                isEdit = isEdit,
                onInput = {
                    deviceParamModel = deviceParamModel.copy(bUpSet = it)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            ItemInputData("B${stringResource(id = R.string.down)}",
                deviceParamModel.bDownSet,
                isEdit = isEdit,
                onInput = {
                    deviceParamModel = deviceParamModel.copy(bDownSet = it)
                })


            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(id = R.string.sensitivity) + "($lightValueMin-$lightValueMax)",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center, modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.height(12.dp))

            ItemInputData(
                name = "A${stringResource(id = R.string.up)}",
                value = deviceParamModel.aUpSensitivity,
                isEdit = isEdit,
                overflow = TextOverflow.Visible,
                onInput = {
                    deviceParamModel = deviceParamModel.copy(aUpSensitivity = it)

                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            ItemInputData(
                name = "A${stringResource(id = R.string.down)}",
                value = deviceParamModel.aDownSensitivity,
                isEdit = isEdit,
                onInput = {
                    deviceParamModel = deviceParamModel.copy(aDownSensitivity = it)

                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ItemInputData(
                name = "B${stringResource(id = R.string.up)}",
                value = deviceParamModel.bUpSensitivity,
                isEdit = isEdit,
                onInput = {
                    deviceParamModel = deviceParamModel.copy(bUpSensitivity = it)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            ItemInputData("B${stringResource(id = R.string.down)}",
                deviceParamModel.bDownSensitivity,
                isEdit = isEdit,
                onInput = {
                    deviceParamModel = deviceParamModel.copy(bDownSensitivity = it)
                })

            Spacer(modifier = Modifier.height(20.dp))


            if (isEdit) {
                Row {
                    BaseButton(
                        stringResource(id = R.string.save),
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                        ),
                        isPaddingV = false
                    ) {


                        if (deviceParamModel.isError()) {
                            ToastUtil.show(context, context.getString(R.string.input_error))
                            return@BaseButton
                        }
                        if (deviceParamModel.isOverLimit()) {
                            ToastUtil.show(context, context.getString(R.string.over_limit))
                            return@BaseButton
                        }
                        isEdit = false
                        onSave(deviceParamModel)

                    }

                    Spacer(modifier = Modifier.width(16.dp))
                    BaseButton(
                        stringResource(id = R.string.cancel),
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                        ),
                        isPaddingV = false
                    ) {
                        isEdit = false
                        deviceParamModel = model
                    }
                }

            } else {
                Row {
                    BaseButton(
                        stringResource(id = R.string.edit),
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                        ),
                        isPaddingV = false
                    ) {
                        isEdit = true
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    BaseButton(
                        stringResource(id = R.string.select),
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                        ),
                        isPaddingV = false
                    ) {
                        onSel(deviceParamModel)
                    }


                }
            }

        } else {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                BaseButton(
                    stringResource(id = R.string.cur_data),
                    modifier = Modifier.width(108.dp),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color.White,
                    ),
                    isPaddingV = false
                ) {
                    deviceParamModel =
                        curModel.copy(name = context.getString(R.string.config) + (index + 1))
                    isEdit = true
                }
                Spacer(modifier = Modifier.height(16.dp))
                BaseButton(
                    stringResource(id = R.string.custom),
                    modifier = Modifier.width(108.dp),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color.White,
                    ),
                    isPaddingV = false
                ) {
                    deviceParamModel =
                        DeviceParamModel(name = context.getString(R.string.config) + (index + 1))
                    isEdit = true
                }


            }

        }


    }


}


@Composable
private fun ItemInputData(
    name: String = "",
    value: String = "",
    isEdit: Boolean = false,
    isOnlyNum: Boolean = true,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    onInput: (String) -> Unit = {},
    onClick: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .height(26.dp)
            .padding(horizontal = 30.dp)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$name: ",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.width(50.dp))
        if (isEdit) {
            BasicTextField(
                value = value,
                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (isOnlyNum) KeyboardType.Number else KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                singleLine = true,
                onValueChange = {
                    onInput(it)
                },

                modifier = Modifier
                    .weight(1.5f)
                    .height(26.dp)
                    .background(color = inputBgWhite)
                    .wrapContentSize(Alignment.Center)
                    .padding(horizontal = 8.dp)
                    .onFocusChanged {
                    },
            )


        } else {
            Text(
                modifier = Modifier
                    .weight(1.5f)
                    .padding(horizontal = 12.dp),
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (overflow == TextOverflow.Ellipsis) 1 else 2,
                overflow = overflow,
                textAlign = TextAlign.Center
            )

        }

    }

}

@Preview
@Composable
fun  test(){

    val readString = "FAAF2903FFFF270FEAAE"
    val inte: String = readString.substring(8, 12).toInt(16).toString()
    val deci: String = readString.substring(12, 16).toInt(16).toString()
    val dur = remember {
        mutableDoubleStateOf("$inte.$deci".toDouble())
    }


    Text(text = dur.value.toString())
}

