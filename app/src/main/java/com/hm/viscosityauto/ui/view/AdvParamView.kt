package com.hm.viscosityauto.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.hm.viscosityauto.model.AdvParamModel
import com.hm.viscosityauto.ui.theme.keyBoardBg
import com.hm.viscosityauto.utils.SPUtils
import com.hm.viscosityauto.utils.ToastUtil
import com.hm.viscosityauto.vm.SettingVM
import kotlinx.coroutines.launch


@Composable
fun AdvParamView(vm: SettingVM = viewModel()) {

    val context = LocalContext.current

    val scope  = rememberCoroutineScope()

    var advParamModel by remember {
        mutableStateOf(
            Gson().fromJson(
                SPUtils.getInstance().getString("advParamModel", Gson().toJson(AdvParamModel())),
                AdvParamModel::class.java
            )
        )
    }


    Column {


        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(id = R.string.advanced_param),
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.weight(1f))

            BaseButton(stringResource(id = R.string.reset)) {
                advParamModel = AdvParamModel()

            }

            Spacer(modifier = Modifier.width(8.dp))
            BaseButton(stringResource(id = R.string.save)) {
                if (!advParamModel.isOk()){
                    ToastUtil.show(context, context.getString(R.string.input_error))
                    return@BaseButton
                }

                SPUtils.getInstance().put("advParamModel", Gson().toJson(advParamModel))
                vm.advParamModel = advParamModel

                scope.launch {
                    vm.setAdvParam(advParamModel)
                    ToastUtil.show(context, context.getString(R.string.save_success))
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))



        Text(
            text = stringResource(id = R.string.clean_empty_setting),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Text(
                text = stringResource(id = R.string.motor_speed)+"(Kpa)",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = advParamModel.emptySpeed,
                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .size(100.dp, 32.dp)
                    .background(color = keyBoardBg)
                    .wrapContentSize(Alignment.Center)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                onValueChange = {
                    advParamModel = advParamModel.copy(emptySpeed = it)
                })

            Spacer(modifier = Modifier.width(32.dp))
            Text(
                text = stringResource(id = R.string.extract_duration)+"(s)",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = advParamModel.emptyExtractDuration,
                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .size(100.dp, 32.dp)
                    .background(color = keyBoardBg)
                    .wrapContentSize(Alignment.Center)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                onValueChange = {
                    advParamModel = advParamModel.copy(emptyExtractDuration = it)
                })

        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Text(
                text = stringResource(id = R.string.extract_interval)+"(s)",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = advParamModel.emptyExtractInterval,
                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .size(100.dp, 32.dp)
                    .background(color = keyBoardBg)
                    .wrapContentSize(Alignment.Center)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                onValueChange = {
                    advParamModel = advParamModel.copy(emptyExtractInterval = it)
                })

            Spacer(modifier = Modifier.width(32.dp))
            Text(
                text = stringResource(id = R.string.drying_duration)+"(s)",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = advParamModel.emptyDryingDuration,
                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .size(100.dp, 32.dp)
                    .background(color = keyBoardBg)
                    .wrapContentSize(Alignment.Center)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                onValueChange = {
                    advParamModel = advParamModel.copy(emptyDryingDuration = it)
                })

        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.clean_setting),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Text(
                text = stringResource(id = R.string.motor_speed)+"(Kpa)",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = advParamModel.cleanSpeed,
                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .size(100.dp, 32.dp)
                    .background(color = keyBoardBg)
                    .wrapContentSize(Alignment.Center)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                onValueChange = {
                    advParamModel = advParamModel.copy(cleanSpeed = it)
                })

            Spacer(modifier = Modifier.width(32.dp))
            Text(
                text = stringResource(id = R.string.drying_duration)+"(s)",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = advParamModel.cleanDryingDuration,
                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .size(100.dp, 32.dp)
                    .background(color = keyBoardBg)
                    .wrapContentSize(Alignment.Center)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                onValueChange = {
                    advParamModel = advParamModel.copy(cleanDryingDuration = it)
                })

        }


        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.decomp_setting),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Text(
                text = stringResource(id = R.string.decomp_duration)+"(s)",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = advParamModel.decompDuration,
                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .size(100.dp, 32.dp)
                    .background(color = keyBoardBg)
                    .wrapContentSize(Alignment.Center)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                onValueChange = {
                    advParamModel = advParamModel.copy(decompDuration = it)
                })
        }



        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.drying_setting),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Text(
                text = stringResource(id = R.string.drying_duration)+"(s)",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = advParamModel.dryingDuration,
                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .size(100.dp, 32.dp)
                    .background(color = keyBoardBg)
                    .wrapContentSize(Alignment.Center)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                onValueChange = {
                    advParamModel = advParamModel.copy(dryingDuration = it)
                })
        }



    }
}