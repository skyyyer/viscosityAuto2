package com.hm.viscosityauto.ui.view

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import com.hm.viscosity.model.MediumModel
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.page.InputView
import com.hm.viscosityauto.ui.theme.buttonEnd
import com.hm.viscosityauto.ui.theme.buttonStart
import com.hm.viscosityauto.ui.theme.cardBg
import com.hm.viscosityauto.ui.theme.cardBgBlue
import com.hm.viscosityauto.ui.theme.cardBgGray
import com.hm.viscosityauto.ui.theme.keyBoardBg
import com.hm.viscosityauto.ui.theme.textColor
import com.hm.viscosityauto.ui.theme.textColorBlue
import com.hm.viscosityauto.ui.theme.textColorGray
import com.hm.viscosityauto.ui.view.click.longClick
import com.hm.viscosityauto.ui.view.click.noMulClick
import com.hm.viscosityauto.utils.LimitUtil
import com.hm.viscosityauto.utils.SPUtils
import com.hm.viscosityauto.vm.SettingVM

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MediumView(
    vm: SettingVM = viewModel(),
) {

    val context = LocalContext.current

    val addMediumDialog = remember {
        mutableStateOf(false)
    }


    val delMediumDialog = remember {
        mutableStateOf(false)
    }
    var selMediumIndex by remember {
        mutableIntStateOf(0)
    }


    //介质
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(id = R.string.medium),
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.weight(1f))

//            Icon(
//                imageVector = Icons.Filled.Add,
//                contentDescription = null,
//                modifier = Modifier
//                    .size(40.dp)
//                    .padding(5.dp)
//                    .clickable {
//                        if (vm.mediumList.size >= 20) {
//                            Toast
//                                .makeText(
//                                    context,
//                                    context.getString(R.string.over_max_value),
//                                    Toast.LENGTH_SHORT
//                                )
//                                .show()
//                            return@clickable
//                        }
//
//                        addMediumDialog.value = true
//                    }
//            )
        }

        Spacer(modifier = Modifier.height(40.dp))


        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
                .background(Color.Transparent),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            vm.mediumList.forEachIndexed { index, model ->

                ItemLab(
                    title = model.name,
                    isSle = model.isSel,
                    isCanDel = model.isCanDel,
                    onClick = {
                        vm.mediumList.forEach {
                            it.isSel = false
                        }
                        vm.mediumList[index] =
                            vm.mediumList[index].copy(isSel = true)

                        vm.setMedium(vm.mediumList[index].p.toInt())
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
        Spacer(modifier = Modifier.height(50.dp))

        BaseButton(stringResource(id = R.string.medium_add)) {
            addMediumDialog.value = true
        }

        Spacer(modifier = Modifier.width(20.dp))

    }

    //添加介质
    BaseDialog(dialogState = addMediumDialog, onDismissRequest = {
        val index = vm.mediumList.indexOfFirst {
            it.isSel
        }
        vm.setMedium(vm.mediumList[index].p.toInt())

    }) {
        AddMediumView(vm.curTemperature, vm.heatingState, onConfirm = { name, p ->
            if (name.isEmpty() || p.isEmpty()) {
                Toast.makeText(
                    context,
                    context.getString(R.string.input_completely),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                addMediumDialog.value = false

                vm.mediumList.add(MediumModel(p, name, isSel = false, isCanDel = true))
                SPUtils.getInstance().put("mediumInfo", Gson().toJson(vm.mediumList))
            }

        }, onDebug = {
            vm.setMedium(it.toInt())
        }, onCancel = {
            addMediumDialog.value = false

        }, setT = {
            vm.setTemperature(it)
        }, stopTemperature = {
            vm.stopTemperature()
        })
    }


    //删除弹框
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
                text = vm.mediumList[selMediumIndex].name + "(${vm.mediumList[selMediumIndex].p})",
                style = MaterialTheme.typography.bodyLarge.copy(color = textColorGray)
            )

            Spacer(modifier = Modifier.height(26.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                BaseButton(title = stringResource(id = R.string.cancel), isNegativeStyle = true) {
                    delMediumDialog.value = false
                }
                Spacer(modifier = Modifier.width(16.dp))

                BaseButton(title = stringResource(id = R.string.confirm)) {
                    delMediumDialog.value = false

                    if (vm.mediumList[selMediumIndex].isSel) {
                        vm.mediumList[0] = vm.mediumList[0].copy(isSel = true)
                    }
                    vm.mediumList.removeAt(selMediumIndex)


                    SPUtils.getInstance()
                        .put("mediumInfo", Gson().toJson(vm.mediumList))
                }

            }

        }

    }, dialogState = delMediumDialog)

}


@Composable
fun ItemLab(
    title: String,
    isSle: Boolean,
    isCanDel: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .background(if (isSle) cardBgBlue else cardBgGray, shape = RoundedCornerShape(5.dp))
            .longClick(onClick = {
                onClick()
            }, onLongClick = {
                onLongClick()
            })
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = if (!isCanDel) {
                if (title == "硅油")
                    context.getString(R.string.medium_silicone_oil) else context.getString(R.string.medium_water)
            } else title,
            style = MaterialTheme.typography.bodyLarge.copy(color = if (isSle) Color.White else textColor),
        )
    }
}


/**
 * 添加介质 弹框
 */
@Composable
fun AddMediumView(
    curTemperature: String,
    heatingState: Int,
    onConfirm: (name: String, p: String) -> Unit,
    onCancel: () -> Unit,
    onDebug: (p: String) -> Unit,
    stopTemperature: () -> Unit,
    setT: (String) -> Unit,
) {

    val context = LocalContext.current

    var name by remember {
        mutableStateOf("")
    }
    var p by remember {
        mutableStateOf("")
    }
    var setTemperature by remember {
        mutableStateOf("40")
    }



    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .width(600.dp)
                .background(color = Color.White, shape = RoundedCornerShape(5.dp))
                .padding(vertical = 28.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.medium_add),
                style = MaterialTheme.typography.titleMedium.copy(textColorBlue),

                )
            Spacer(modifier = Modifier.height(32.dp))

            //温度
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp), verticalAlignment = Alignment.Bottom) {

                Text(
                    text = stringResource(id = R.string.set_temperature) + ":  ",
                    style = MaterialTheme.typography.bodyLarge
                )
                InputView(value = setTemperature, onValueChange = { setTemperature = it})

                Spacer(modifier = Modifier.width(8.dp))

                if (heatingState != 0) {
                    Spacer(modifier = Modifier.width(8.dp))

                    Row(
                        modifier = Modifier
                            .size(82.dp, 32.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        buttonStart,
                                        buttonEnd,
                                    )
                                ), shape = RoundedCornerShape(5.dp)
                            )
                            .noMulClick {
                                stopTemperature()
                            }
                        ,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            maxLines = 1,
                            text = stringResource(id = R.string.end),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White,
                            ),
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        modifier = Modifier
                            .size(82.dp, 32.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        buttonStart,
                                        buttonEnd,
                                    )
                                ), shape = RoundedCornerShape(5.dp)
                            )
                            .noMulClick {
                                if (LimitUtil.isOverLimit(context, setTemperature)) {
                                    return@noMulClick
                                }

                                setT(setTemperature)
                            }
                        ,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            maxLines = 1,
                            text = stringResource(id = R.string.start),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White,
                            ),
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = stringResource(id = R.string.cur_temperature) + ":  ",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = curTemperature + " ℃",
                    style = MaterialTheme.typography.bodyLarge
                )
                if (heatingState != 0) {
                    Spacer(modifier = Modifier.width(8.dp))

                    Image(
                        painter = painterResource(id = if (heatingState == 1) R.mipmap.heating_icon else R.mipmap.keep_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }

            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.medium_name),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(80.dp)
                )

                InputView(value = name, width = 200.dp, onValueChange = {
                    name = if (it.length > 5) {
                        it.substring(0, 5)
                    } else {
                        it
                    }
                })

            }
            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.medium_pid),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(80.dp)
                )

                InputView(value =  p, width = 200.dp, onValueChange = {
                    p = it
                })

            }


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
                BaseButton(title = stringResource(id = R.string.debugging)) {
                    if (p.toIntOrNull() == null) {
                        Toast.makeText(
                            context, context.getString(R.string.input_error), Toast
                                .LENGTH_SHORT
                        ).show()
                        return@BaseButton
                    }
                    if (p.toInt() > 25 || p.toInt() < 1) {
                        Toast.makeText(
                            context, context.getString(R.string.over_limit), Toast
                                .LENGTH_SHORT
                        ).show()
                        return@BaseButton
                    }
                    onDebug(p)
                }
                Spacer(modifier = Modifier.width(20.dp))
                BaseButton(title = stringResource(id = R.string.confirm)) {
                    if (p.toIntOrNull() == null) {
                        Toast.makeText(
                            context, context.getString(R.string.input_error), Toast
                                .LENGTH_SHORT
                        ).show()
                        return@BaseButton
                    }
                    if (p.toInt() > 25 || p.toInt() < 1) {
                        Toast.makeText(
                            context, context.getString(R.string.over_limit), Toast
                                .LENGTH_SHORT
                        ).show()
                        return@BaseButton
                    }
                    onConfirm(name, p)
                }

            }

        }

    }
}