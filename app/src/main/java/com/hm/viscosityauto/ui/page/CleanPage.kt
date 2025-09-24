package com.hm.viscosityauto.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asi.nav.Nav
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.theme.cardBg
import com.hm.viscosityauto.ui.theme.keyBoardBg
import com.hm.viscosityauto.ui.view.BaseButton
import com.hm.viscosityauto.ui.view.BaseTitle
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun CleanPage(vm: SettingVM = viewModel()) {
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
            BaseTitle(title = stringResource(id = R.string.manual_clean), onBack = {
                if (vm.stateA != Empty || vm.stateB != Empty) {
                    ToastUtil.show(context, context.getString(R.string.exit_tip))
                    return@BaseTitle
                }
                Nav.back()
            })

            BaseButton(
                title = stringResource(id = R.string.decomp),
                icon = R.mipmap.press_set_icon_white,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 126.dp)
            ) {

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

        Spacer(modifier = Modifier.height(80.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

            ItemCmdView(
                "A",
                vm.stateA,
                cleanDurationA,
                addDurationA,
                modifier = Modifier
                    .width(320.dp),
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
            Spacer(modifier = Modifier.width(82.dp))

            ItemCmdView(
                "B",
                vm.stateB,
                cleanDurationB,
                addDurationB,
                modifier = Modifier
                    .width(320.dp),
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
            .background(cardBg, shape = RoundedCornerShape(12.dp))
            .padding(35.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {


        Row(verticalAlignment = Alignment.Bottom) {
            Image(
                painter = painterResource(id = if (name == "A") R.mipmap.a_icon else R.mipmap.b_icon),
                contentDescription = null,
                modifier = Modifier.size(78.dp)
            )
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(id = R.string.state) + ": " + when (state) {
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
        }

        Spacer(modifier = Modifier.height(32.dp))



        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.clean_duration) + "(s):",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            InputView(value = value1Str, onValueChange = {
                value1Str = it
            })

        }
        Spacer(modifier = Modifier.height(16.dp))


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.add_liquid_duration) + "(s):",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            InputView(value = value2Str, onValueChange = {
                value2Str = it
            })
        }
        Spacer(modifier = Modifier.height(32.dp))

        Row (Modifier.fillMaxWidth()) {
            BaseButton(stringResource(id = R.string.clean),modifier = Modifier.weight(1f)) {
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

            Spacer(modifier = Modifier.width(32.dp))


            BaseButton(stringResource(id = R.string.clean_empty),modifier = Modifier.weight(1f)) {
                if (state != Empty) {
                    ToastUtil.show(context, context.getString(R.string.device_running))
                    return@BaseButton
                }
                onCleanEmpty()
            }


        }

        Spacer(modifier = Modifier.height(16.dp))

        Row (Modifier.fillMaxWidth()){
            BaseButton(stringResource(id = R.string.clean_drying),modifier = Modifier.weight(1f)) {
                if (state != Empty) {
                    ToastUtil.show(context, context.getString(R.string.device_running))
                    return@BaseButton
                }
                onDrying()
            }
            Spacer(modifier = Modifier.width(32.dp))
            BaseButton(stringResource(id = R.string.end),modifier = Modifier.weight(1f)) {
                onEnd()
            }

        }


    }

}