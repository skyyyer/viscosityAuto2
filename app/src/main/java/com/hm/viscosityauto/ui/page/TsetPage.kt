package com.hm.viscosityauto.ui.page

import NoPressStateClick
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asi.nav.Nav
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hm.viscosityauto.MyApp
import com.hm.viscosityauto.R
import com.hm.viscosityauto.model.DurationModel
import com.hm.viscosityauto.model.PassageModel
import com.hm.viscosityauto.model.PointTModel
import com.hm.viscosityauto.ui.theme.GrayBg
import com.hm.viscosityauto.ui.theme.TestCardBg
import com.hm.viscosityauto.ui.theme.cardBgBlue
import com.hm.viscosityauto.ui.theme.cardBgBlue1
import com.hm.viscosityauto.ui.theme.cardBgGray
import com.hm.viscosityauto.ui.theme.cardBgGreen
import com.hm.viscosityauto.ui.theme.cardBgWhite
import com.hm.viscosityauto.ui.theme.inputBgWhite
import com.hm.viscosityauto.ui.theme.keyBoardBg
import com.hm.viscosityauto.ui.theme.textColorBlue
import com.hm.viscosityauto.ui.view.BaseButton
import com.hm.viscosityauto.ui.view.BaseDialog
import com.hm.viscosityauto.ui.view.BasePage
import com.hm.viscosityauto.ui.view.BaseTitle
import com.hm.viscosityauto.ui.view.ItemData
import com.hm.viscosityauto.ui.view.PassageCard
import com.hm.viscosityauto.ui.view.click.doubleClick
import com.hm.viscosityauto.utils.ComputeUtils
import com.hm.viscosityauto.utils.FileUtil
import com.hm.viscosityauto.utils.LimitUtil
import com.hm.viscosityauto.utils.SPUtils
import com.hm.viscosityauto.utils.TimeUtils
import com.hm.viscosityauto.utils.ToastUtil
import com.hm.viscosityauto.vm.CHANNEL_A
import com.hm.viscosityauto.vm.CHANNEL_B

import com.hm.viscosityauto.vm.HeatState
import com.hm.viscosityauto.vm.HeatState.Heating
import com.hm.viscosityauto.vm.HeatState.Keeping
import com.hm.viscosityauto.vm.TestState
import com.hm.viscosityauto.vm.TestState.DecomP
import com.hm.viscosityauto.vm.TestState.Empty
import com.hm.viscosityauto.vm.TestState.Running
import com.hm.viscosityauto.vm.TestVM
import kotlinx.coroutines.delay
import org.jetbrains.annotations.TestOnly
import java.io.File

@Composable
fun TestPage(vm: TestVM = viewModel()) {
    val context = LocalContext.current


    val clickTimer = remember {
        mutableLongStateOf(0)
    }

    val configDialog = remember {
        mutableStateOf(false)
    }

    var selChannel by remember {
        mutableIntStateOf(1)
    }


    var selConfigA by remember {
        mutableIntStateOf(-1)
    }

    var selConfigB by remember {
        mutableIntStateOf(-1)
    }

    val focusManager = LocalFocusManager.current

    var isReady by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        delay(200)
        isReady = true
    }


    LaunchedEffect(vm.heatingState) {
        if (vm.heatingState == Keeping) {
            if (vm.passageModelA.state == Running) {
                vm.startKeepTTimer(
                    context,
                    vm.passageModelA.id,
                    vm.passageModelA.keepTDuration.toInt()
                )

            }
            if (vm.passageModelB.state == Running) {
                vm.startKeepTTimer(
                    context,
                    vm.passageModelB.id,
                    vm.passageModelB.keepTDuration.toInt()
                )
            }

        }else{
            vm.stopKeepTTimer(1)
            vm.stopKeepTTimer(2)
        }
    }


    BasePage {

        //标题

        BaseTitle(title = stringResource(id = R.string.test), onBack = {
            if (vm.passageModelA.state != Empty || vm.passageModelB.state != Empty) {
                ToastUtil.show(context, context.getString(R.string.exit_tip))
                return@BaseTitle
            }
            Nav.back()

//                vm.closeSerialPort()
        })

        Spacer(modifier = Modifier.height(8.dp))

        if (isReady) {
            Row {
                Row(modifier = Modifier.weight(1f)) {
                    PassageCard(name = "A",
                        model = vm.passageModelA,
                        "%.2f".format(vm.ATimekeeping),
                        vm.keepTCountA,
                        vm.showDataOptA,
                        modifier = Modifier.weight(1f),
                        updateModel = {
                            vm.passageModelA = it
                            SPUtils.getInstance()
                                .put(
                                    CHANNEL_A,
                                    Gson().toJson(
                                        vm.passageModelA
                                    )
                                )
                        },
                        onPrint = {
                            vm.printData(context, vm.passageModelA)
                        },
                        onStart = {
                            vm.startPassage(context, vm.passageModelA.id)
                        },
                        onFinish = {
                            vm.endPassage(vm.passageModelA.id)

                        },
                        onConfirm = {
                            vm.passageModelA =
                                vm.passageModelA.copy(
                                    state = Empty,
                                    viscosity = "0.000",
                                    curNum = 0,
                                    duration = "0.00",
                                    curCleanNum = 0,
                                    durationArray = ArrayList()
                                )
                            vm.ATimekeeping = 0.00f
                        },
                        onCompute = {
                            vm.showDataOptA = false
                            vm.passageModelA = vm.passageModelA.copy(durationArray = ArrayList(it), time = TimeUtils.timestampToString(), temperature = vm.setTemperature)
                            vm.passageModelA.computeViscosity()
                            vm.saveDate(vm.passageModelA)

                        })
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (selConfigA == -1) stringResource(id = R.string.sel_config) else stringResource(
                            id = R.string.config
                        ) + (selConfigA + 1), modifier = Modifier.clickable {
                            selChannel = vm.passageModelA.id
                            configDialog.value = true
                        }
                    )
                }

                //温度控制
                Column(
                    modifier = Modifier.width(280.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(40.dp))

//                    Image(
//                        painter = painterResource(id = if (vm.lightState.value) R.mipmap.light_icon else R.mipmap.light_close_icon),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .size(100.dp)
//                            .NoPressStateClick(onClick = {
//                                if (vm.passageModelA.state != HeatState.Empty || vm.passageModelB.state != HeatState.Empty) {
//                                    Toast
//                                        .makeText(
//                                            context,
//                                            context.getString(R.string.testing_tip),
//                                            Toast.LENGTH_SHORT
//                                        )
//                                        .show()
//                                    return@NoPressStateClick
//                                }
//
//                                if (System.currentTimeMillis() - clickTimer.longValue > 5000) {
//                                    clickTimer.longValue = System.currentTimeMillis()
//                                    vm.setLightState(!vm.lightState.value)
//                                } else {
//                                    Toast
//                                        .makeText(
//                                            context,
//                                            context.getString(R.string.click_often_tip),
//                                            Toast.LENGTH_SHORT
//                                        )
//                                        .show()
//                                }
//
//                            })
//                    )


                    Spacer(modifier = Modifier.height(60.dp))




                    Row {
                        Text(
                            text = vm.curTemperature + " ℃",
                            style = MaterialTheme.typography.displaySmall,
                            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
                        )
//
//                        Spacer(modifier = Modifier.width(20.dp))
//
//                        if (vm.heatingState != 0) {
//                            Image(
//                                painter = painterResource(id = if (vm.heatingState == 1) R.mipmap.heating_icon else R.mipmap.keep_icon),
//                                contentDescription = null,
//                                modifier = Modifier
//                                    .size(30.dp)
//                            )
//
//                        }

                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = when (vm.heatingState) {
                            HeatState.Empty -> stringResource(id = R.string.empty)
                            HeatState.Heating -> stringResource(id = R.string.controllingT)
                            HeatState.Keeping -> stringResource(id = R.string.keepingT)
                            else -> ""
                        }, style = MaterialTheme.typography.bodyLarge
                    )


                    Spacer(modifier = Modifier.height(30.dp))


                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(id = R.string.set_temperature) + ":  ",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = textColorBlue,
                                fontSize = 24.sp
                            )
                        )

                        BasicTextField(
                            value = vm.setTemperature,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                focusManager.clearFocus()
                                if (LimitUtil.isOverLimit(context, vm.setTemperature)) {
                                    return@KeyboardActions
                                }
                                vm.setTemperature(vm.setTemperature)
                            }),
                            singleLine = true,
                            enabled = vm.passageModelA.state == 0 && vm.passageModelB.state == 0,
                            modifier = Modifier
                                .size(120.dp, 40.dp)
                                .background(color = keyBoardBg)
                                .wrapContentSize(Alignment.Center)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            onValueChange = {
                                vm.setTemperature = it
                            })


                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (vm.heatingState != 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        BaseButton(title = stringResource(id = R.string.stop_heating)) {
                            if (vm.passageModelA.state != HeatState.Empty || vm.passageModelB.state != HeatState.Empty) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.testing_tip),
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@BaseButton
                            }
                            vm.stopTemperature()

                            vm.stopKeepTTimer(1)
                            vm.stopKeepTTimer(2)

                        }
                    } else {
                        Spacer(modifier = Modifier.width(8.dp))
                        BaseButton(title = stringResource(id = R.string.start_heating)) {
                            if (vm.passageModelA.state != HeatState.Empty || vm.passageModelB.state != HeatState.Empty) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.testing_tip),
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@BaseButton
                            }
                            if (LimitUtil.isOverLimit(context, vm.setTemperature)) {
                                return@BaseButton
                            }
                            vm.setTemperature(vm.setTemperature)

                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    //自动设置
                    Row {

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(id = R.string.auto_empty),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Switch(modifier = Modifier
                                    .height(24.dp),
                                    checked = vm.autoEmpty.value,
                                    colors = SwitchDefaults.colors(
                                        checkedTrackColor = cardBgBlue,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedBorderColor = cardBgGray,
                                        uncheckedTrackColor = cardBgGray
                                    ),
                                    onCheckedChange = {
                                        if (vm.passageModelA.state != HeatState.Empty || vm.passageModelB.state != HeatState.Empty) {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.testing_tip),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@Switch
                                        }
                                        vm.setAutoEmpty(it)
                                    })


                            }

                            Spacer(modifier = Modifier.height(20.dp))


                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(id = R.string.auto_clean),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Switch(modifier = Modifier
                                    .height(24.dp),
                                    checked = vm.autoClean.value,
                                    colors = SwitchDefaults.colors(
                                        checkedTrackColor = cardBgBlue,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedBorderColor = cardBgGray,
                                        uncheckedTrackColor = cardBgGray
                                    ),
                                    onCheckedChange = {
                                        if (vm.passageModelA.state != HeatState.Empty || vm.passageModelB.state != HeatState.Empty) {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.testing_tip),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@Switch
                                        }
                                        vm.setAutoClean(it)
                                    })


                            }

                        }

                        Spacer(modifier = Modifier.width(20.dp))


                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(id = R.string.auto_print),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Switch(modifier = Modifier
                                    .height(24.dp),
                                    checked = vm.autoPrint.value,
                                    colors = SwitchDefaults.colors(
                                        checkedTrackColor = cardBgBlue,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedBorderColor = cardBgGray,
                                        uncheckedTrackColor = cardBgGray
                                    ),
                                    onCheckedChange = {
                                        if (vm.passageModelA.state != HeatState.Empty || vm.passageModelB.state != HeatState.Empty) {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.testing_tip),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@Switch
                                        }
                                        vm.setAutoPrint(it)
                                    })


                            }
                            Spacer(modifier = Modifier.height(20.dp))


                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(id = R.string.data_optimization),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Switch(modifier = Modifier
                                    .height(24.dp),
                                    checked = vm.dataOpt.value,
                                    colors = SwitchDefaults.colors(
                                        checkedTrackColor = cardBgBlue,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedBorderColor = cardBgGray,
                                        uncheckedTrackColor = cardBgGray
                                    ),
                                    onCheckedChange = {
                                        if (vm.passageModelA.state != HeatState.Empty || vm.passageModelB.state != HeatState.Empty) {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.testing_tip),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@Switch
                                        }
                                        vm.setDataOpt(it)
                                    })


                            }
                        }


                    }

                }


                //配置
                Row(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (selConfigB == -1) stringResource(id = R.string.sel_config) else stringResource(
                            id = R.string.config
                        ) + (selConfigB + 1), modifier = Modifier.clickable {
                            selChannel = vm.passageModelB.id
                            configDialog.value = true
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    PassageCard(name = "B",
                        model = vm.passageModelB,
                        "%.2f".format(vm.BTimekeeping),
                        vm.keepTCountB,
                        vm.showDataOptB,
                        modifier = Modifier.weight(1f),
                        updateModel = {
                            vm.passageModelB = it
                            SPUtils.getInstance()
                                .put(
                                    CHANNEL_B,
                                    Gson().toJson(
                                        vm.passageModelB
                                    )
                                )
                        },
                        onPrint = {
                            vm.printData(context, vm.passageModelB)
                        },
                        onStart = {
                            vm.startPassage(context, vm.passageModelB.id)
                        },
                        onFinish = {
                            vm.endPassage(vm.passageModelB.id)
                        },
                        onConfirm = {
                            vm.passageModelB =
                                vm.passageModelB.copy(
                                    state = Empty,
                                    viscosity = "0.000",
                                    curNum = 0,
                                    duration = "0.00",
                                    curCleanNum = 0,
                                    durationArray = ArrayList()
                                )
                            vm.BTimekeeping = 0.00f
                        },
                        onCompute = {
                            vm.showDataOptB = false
                            vm.passageModelB = vm.passageModelB.copy(durationArray = ArrayList(it), time = TimeUtils.timestampToString(), temperature = vm.setTemperature)
                            vm.passageModelB.computeViscosity()
                            vm.saveDate(vm.passageModelB)
                        })
                }
            }
        }


    }

    //配置列表
    BaseDialog(contentView = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp)
                .background(color = cardBgGray, shape = RoundedCornerShape(5.dp))
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LazyRow(content = {
                itemsIndexed(vm.configList, key = { index, item ->
                    index
                }) { index: Int, item: PassageModel ->
                    ConfigItemView(index,
                        PassageModel(
                            constant = vm.passageModelA.constant,
                            testCount = vm.passageModelA.testCount,
                            cleanTimes = vm.passageModelA.cleanTimes,
                            keepTDuration = vm.passageModelA.keepTDuration,
                            curCleanNum = vm.passageModelA.curCleanNum,
                            cleanDuration = vm.passageModelA.cleanDuration,
                            addDuration = vm.passageModelA.addDuration,
                            extractDuration = vm.passageModelA.extractDuration,
                            extractInterval = vm.passageModelA.extractInterval,
                            motorSpeed = vm.passageModelA.motorSpeed,
                        ),
                        PassageModel(
                            constant = vm.passageModelB.constant,
                            testCount = vm.passageModelB.testCount,
                            cleanTimes = vm.passageModelB.cleanTimes,
                            keepTDuration = vm.passageModelB.keepTDuration,
                            curCleanNum = vm.passageModelB.curCleanNum,
                            cleanDuration = vm.passageModelB.cleanDuration,
                            addDuration = vm.passageModelB.addDuration,
                            extractDuration = vm.passageModelB.extractDuration,
                            extractInterval = vm.passageModelB.extractInterval,
                            motorSpeed = vm.passageModelB.motorSpeed,
                        ),
                        item, if (selChannel == vm.passageModelA.id) {
                            selConfigA == index
                        } else {
                            selConfigB == index
                        }, onSel = {
                            if (selChannel == vm.passageModelA.id) {
                                selConfigA = index
                                vm.passageModelA = vm.configList[index].copy(id = 1)
                                SPUtils.getInstance()
                                    .put(
                                        CHANNEL_A,
                                        Gson().toJson(
                                            vm.passageModelA
                                        )
                                    )
                            } else {
                                selConfigB = index
                                vm.passageModelB = vm.configList[index].copy(id = 2)
                                SPUtils.getInstance()
                                    .put(
                                        CHANNEL_B,
                                        Gson().toJson(
                                            vm.passageModelB
                                        )
                                    )
                            }
                        }, onSave = {
                            vm.configList[index] = it
                            SPUtils.getInstance().put("configInfo", Gson().toJson(vm.configList))
                        }
                    )

                }
            })


        }

    }, dialogState = configDialog)

}


@Composable
fun ItemDurationView(
    index: Int,
    item: DurationModel,
    isEdit: Boolean = false,
    isSel: Boolean = false,
    onSel: (Boolean) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (isEdit) {
            Checkbox(
                checked = isSel,
                onCheckedChange = {
                    onSel(it)
                },
                colors = CheckboxDefaults.colors(checkedColor = cardBgBlue),
                modifier = Modifier.size(30.dp)
            )
        } else {
            Spacer(modifier = Modifier.width(30.dp))
        }



        Text(
            text = stringResource(id = R.string.number_start) + (index + 1) + stringResource(id = R.string.number_end)+":  ",
            style = if (item.enable) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyLarge.copy(
                color = GrayBg,
                textDecoration = if (item.derelict) TextDecoration.LineThrough else null

            ),
            textAlign = TextAlign.Center,
        )

        Text(
            text = "%.2f".format(item.duration) + "(s)",
            style = if (item.enable) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyLarge.copy(
                color = GrayBg,
                textDecoration = if (item.derelict) TextDecoration.LineThrough else null
            ),
            textAlign = TextAlign.Center,
        )

//        Text(
//            text = item.temperature,
//            style = if (item.enable) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyLarge.copy(
//                color = GrayBg
//            ),
//            textAlign = TextAlign.Center,
//            modifier = Modifier.weight(2f)
//        )
    }
}


@Composable
private fun ConfigItemView(
    index: Int,
    modelA: PassageModel,
    modelB: PassageModel,
    model: PassageModel,
    isSel: Boolean = false,
    onSel: () -> Unit, onSave: (PassageModel) -> Unit,
) {
    val context = LocalContext.current

    var passageModel by remember {
        mutableStateOf(model)
    }

    var isEdit by remember {
        mutableStateOf(false)
    }


    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .size(340.dp, 550.dp)
            .background(
                color = if (isSel) cardBgBlue1 else cardBgWhite,
                shape = RoundedCornerShape(5.dp)
            )
            .padding(horizontal = 10.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text(
            text = stringResource(id = R.string.config) + (index + 1),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))



        if (passageModel.testCount != "0") {
            ItemData(
                name = stringResource(id = R.string.viscosity_constant)+"(mm²/S²)",
                value = passageModel.constant,
                isEdit = isEdit,
                overflow = TextOverflow.Visible,
                onInput = {
                    passageModel = passageModel.copy(constant = it)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            ItemData(
                name = stringResource(id = R.string.keep_t_duration) + "(s)",
                value = passageModel.keepTDuration,
                isEdit = isEdit,
                onInput = {
                    passageModel = passageModel.copy(keepTDuration = it)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ItemData(
                name = stringResource(id = R.string.test_count),
                value = passageModel.testCount,
                isEdit = isEdit,
                onInput = {
                    passageModel = passageModel.copy(testCount = it)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            ItemData(stringResource(id = R.string.extract_duration) + "(s)",
                passageModel.extractDuration,
                isEdit = isEdit,
                onInput = {
                    passageModel = passageModel.copy(extractDuration = it)

                })


            Spacer(modifier = Modifier.height(12.dp))

            ItemData(stringResource(id = R.string.extract_interval) + "(s)",
                passageModel.extractInterval,
                isEdit = isEdit,
                onInput = {
                    passageModel = passageModel.copy(extractInterval = it)

                })

            Spacer(modifier = Modifier.height(12.dp))

            ItemData(stringResource(id = R.string.clean_times),
                passageModel.cleanTimes,
                isEdit = isEdit,
                onInput = {
                    passageModel = passageModel.copy(cleanTimes = it)

                })

            Spacer(modifier = Modifier.height(12.dp))

            ItemData(stringResource(id = R.string.clean_duration) + "(s)",
                passageModel.cleanDuration,
                isEdit = isEdit,
                onInput = {
                    passageModel = passageModel.copy(cleanDuration = it)

                })

            Spacer(modifier = Modifier.height(12.dp))

            ItemData(stringResource(id = R.string.add_liquid_duration) + "(s)",
                passageModel.addDuration,
                isEdit = isEdit,
                onInput = {
                    passageModel = passageModel.copy(addDuration = it)

                })

            Spacer(modifier = Modifier.height(12.dp))

            ItemData(stringResource(id = R.string.motor_speed)+"(Kpa)",
                passageModel.motorSpeed,
                isEdit = isEdit,
                onInput = {
                    passageModel = passageModel.copy(motorSpeed = it)
                })
            Spacer(modifier = Modifier.height(20.dp))


            if (isEdit) {
                Row {
                    BaseButton(stringResource(id = R.string.save)) {
                        if (passageModel.isReady()) {
                            isEdit = false
                            onSave(passageModel)
                        } else {
                            ToastUtil.show(context, context.getString(R.string.input_error))
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                    BaseButton(stringResource(id = R.string.cancel)) {
                        isEdit = false
                        passageModel = model
                    }
                }

            } else {
                Row {
                    BaseButton(stringResource(id = R.string.edit)) {
                        isEdit = true
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    BaseButton(stringResource(id = R.string.select)) {
                        onSel()
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

                BaseButton("A") {
                    passageModel = modelA
                    isEdit = true
                }
                Spacer(modifier = Modifier.height(16.dp))
                BaseButton("B") {
                    passageModel = modelB
                    isEdit = true
                }
                Spacer(modifier = Modifier.height(16.dp))
                BaseButton(stringResource(id = R.string.custom)) {
                    passageModel = PassageModel()
                    isEdit = true
                }


            }

        }


    }


}

