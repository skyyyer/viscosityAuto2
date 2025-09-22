package com.hm.viscosityauto.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import com.hm.viscosityauto.ui.theme.keyBoardBg
import com.hm.viscosityauto.utils.SPUtils
import com.hm.viscosityauto.utils.ToastUtil
import com.hm.viscosityauto.vm.SettingVM
import com.hm.viscosityauto.vm.TestCMD.CMD_Clean
import com.hm.viscosityauto.vm.TestCMD.CMD_CleanEmpty
import com.hm.viscosityauto.vm.TestCMD.CMD_DecomP
import com.hm.viscosityauto.vm.TestCMD.CMD_Drying
import com.hm.viscosityauto.vm.TestCMD.CMD_Stop
import com.hm.viscosityauto.vm.TestState.Clean
import com.hm.viscosityauto.vm.TestState.CleanEmpty
import com.hm.viscosityauto.vm.TestState.DecomP
import com.hm.viscosityauto.vm.TestState.Drying
import com.hm.viscosityauto.vm.TestState.Empty
import com.hm.viscosityauto.vm.TestVM
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun CleanView(vm: SettingVM = viewModel()) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    var cleanDurationA by remember {
        mutableStateOf("5")
    }

    var cleanDurationB by remember {
        mutableStateOf("5")
    }
    var addDurationA by remember {
        mutableStateOf("5")
    }
    var addDurationB by remember {
        mutableStateOf("5")
    }

    Column {
        Row {
            Column(
                Modifier
                    .width(200.dp)
                    .height(400.dp), verticalArrangement = Arrangement.SpaceEvenly) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.state),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.clean_duration) + "(s)",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.add_liquid_duration) + "(s)",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(50.dp))

            ItemCmdView(
                "A",
                vm.stateA,
                cleanDurationA,
                addDurationA,
                modifier = Modifier
                    .height(400.dp)
                    .width(100.dp),
                onClean = { clean, add ->
                    cleanDurationA = clean
                    addDurationA = add
                    vm.stateA = Clean
                    scope.launch {
                        vm.setParam(cleanDurationA, cleanDurationB, addDurationA, addDurationB)
                        delay(50)
                        vm.setState(1, CMD_Clean)
                    }
                }, onCleanEmpty = {
                    vm.stateA = CleanEmpty
                    vm.setState(1, CMD_CleanEmpty)
                }, onDrying = {
                    if (vm.stateB == Drying) {
                        ToastUtil.show(context, context.getString(R.string.wait_drying_tip))
                        return@ItemCmdView
                    }
                    vm.stateA = Drying
                    vm.setState(1, CMD_Drying)
                    vm.startDryingTimer(1)


                }, onEnd = {
                    vm.stateA = Empty
                    vm.stopDryingTimer()
                    vm.stopDecomPTimer()
                    vm.setState(1, CMD_Stop)
                })
            Spacer(modifier = Modifier.width(50.dp))

            ItemCmdView(
                "B",
                vm.stateB,
                cleanDurationB,
                addDurationB,
                modifier = Modifier
                    .height(400.dp)
                    .width(100.dp),
                onClean = { clean, add ->
                    cleanDurationB = clean
                    addDurationB = add
                    vm.stateB = Clean
                    scope.launch {
                        vm.setParam(cleanDurationA, cleanDurationB, addDurationA, addDurationB)
                        delay(50)
                        vm.setState(2, CMD_Clean)
                    }
                }, onCleanEmpty = {
                    vm.stateB = CleanEmpty
                    vm.setState(2, CMD_CleanEmpty)
                }, onDrying = {
                    if (vm.stateA == Drying) {
                        ToastUtil.show(context, context.getString(R.string.wait_drying_tip))
                        return@ItemCmdView
                    }
                    vm.stateB = Drying
                    vm.setState(2, CMD_Drying)
                    vm.startDryingTimer(2)
                }, onEnd = {
                    vm.stateB = Empty
                    vm.stopDryingTimer()
                    vm.stopDecomPTimer()
                    vm.setState(2, CMD_Stop)
                })

        }
        Spacer(modifier = Modifier.height(20.dp))

        BaseButton(stringResource(id = R.string.decomp), modifier = Modifier
            .padding(start = 250.dp)
            .width(250.dp)) {

            if (vm.stateA != Empty || vm.stateB != Empty) {
                ToastUtil.show(context, context.getString(R.string.device_running))
                return@BaseButton
            }

            vm.stateA = DecomP
            vm.stateB = DecomP
            vm.setState(1, CMD_DecomP)
            vm.startDecomPTimer()
        }

    }
}

@Composable
private fun ItemCmdView(
    name: String = "",
    state: Int = 0,
    cleanDuration: String,
    addDuration: String,
    modifier: Modifier,
    onClean: (String, String) -> Unit,
    onCleanEmpty: () -> Unit,
    onDrying: () -> Unit,
    onEnd: () -> Unit,
) {
    val context = LocalContext.current

    var value1Str by remember {
        mutableStateOf(cleanDuration)
    }
    var value2Str by remember {
        mutableStateOf(addDuration)
    }

    Column(
        modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Text(
            text = when (state) {
                Empty -> stringResource(id = R.string.empty)
                Clean -> stringResource(id = R.string.clean)
                CleanEmpty -> stringResource(id = R.string.clean_empty)
                Drying -> stringResource(id = R.string.clean_drying)
                DecomP -> stringResource(id = R.string.decomp)
                else -> stringResource(id = R.string.empty)
            },
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


        BaseButton(stringResource(id = R.string.clean)) {
            if (state != Empty) {
                ToastUtil.show(context, context.getString(R.string.device_running))
                return@BaseButton
            }

            if (value1Str.toIntOrNull() == null || value2Str.toIntOrNull() == null) {
                ToastUtil.show(context, context.getString(R.string.input_error))
                return@BaseButton
            }

            if (value1Str.toInt() < 0 || value2Str.toInt() < 0) {
                ToastUtil.show(
                    context, context.getString(R.string.over_limit)
                )
                return@BaseButton
            }

            onClean(value1Str, value2Str)
        }



        BaseButton(stringResource(id = R.string.clean_empty)) {
            if (state != Empty) {
                ToastUtil.show(context, context.getString(R.string.device_running))
                return@BaseButton
            }
            onCleanEmpty()
        }



        BaseButton(stringResource(id = R.string.clean_drying)) {
            if (state != Empty) {
                ToastUtil.show(context, context.getString(R.string.device_running))
                return@BaseButton
            }
            onDrying()
        }

        BaseButton(stringResource(id = R.string.end)) {
            onEnd()
        }


    }

}