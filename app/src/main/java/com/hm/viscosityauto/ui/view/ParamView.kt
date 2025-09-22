package com.hm.viscosityauto.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.theme.keyBoardBg
import com.hm.viscosityauto.utils.SPUtils
import com.hm.viscosityauto.utils.ToastUtil
import com.hm.viscosityauto.vm.SettingVM
import com.hm.viscosityauto.vm.TestState.CleanEmpty
import com.hm.viscosityauto.vm.TestVM
import kotlinx.coroutines.delay


private const val setValueMax = 4095
private const val lightValueMax = 100


@Composable
fun ParamView(vm: SettingVM = viewModel()) {

    DisposableEffect(Unit){
        vm.startABValueUp(true)
        onDispose {
           vm.startABValueUp(false)
        }
    }

    Column {

        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.height(300.dp), verticalArrangement = Arrangement.SpaceEvenly) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.detected_value),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.set_value) + "(0-$setValueMax)",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.sensitivity) + "(0-$lightValueMax)",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }

            ItemView(
                name = "A${stringResource(id = R.string.up)}",
                modifier = Modifier
                    .weight(1f)
                    .height(300.dp),
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

            ItemView(
                name = "A${stringResource(id = R.string.down)}",
                modifier = Modifier
                    .weight(1f)
                    .height(300.dp),
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

            ItemView(
                name = "B${stringResource(id = R.string.up)}",
                modifier = Modifier
                    .weight(1f)
                    .height(300.dp),
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

            ItemView(
                name = "B${stringResource(id = R.string.down)}",
                modifier = Modifier
                    .weight(1f)
                    .height(300.dp),
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

    Column(
        modifier
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )


        Text(
            text = detectedValue,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        BasicTextField(
            value = value1Str,
            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .size(100.dp, 32.dp)
                .background(color = keyBoardBg)
                .wrapContentSize(Alignment.Center)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            onValueChange = {
                value1Str = it
            })


        BasicTextField(
            value = value2Str,
            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .size(100.dp, 32.dp)
                .background(color = keyBoardBg)
                .wrapContentSize(Alignment.Center)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            onValueChange = {
                value2Str = it
            })


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