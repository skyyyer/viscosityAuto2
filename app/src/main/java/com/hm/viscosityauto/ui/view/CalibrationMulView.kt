package com.hm.viscosityauto.ui.view

import NoPressStateClick
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hm.viscosityauto.model.PointTModel
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.page.InputView
import com.hm.viscosityauto.ui.theme.buttonEnd
import com.hm.viscosityauto.ui.theme.buttonStart
import com.hm.viscosityauto.ui.theme.cardBgBlue
import com.hm.viscosityauto.ui.theme.cardBgGray
import com.hm.viscosityauto.ui.theme.inputBgWhite
import com.hm.viscosityauto.ui.theme.keyBoardBg
import com.hm.viscosityauto.ui.theme.textColorBlue
import com.hm.viscosityauto.ui.theme.textColorGray
import com.hm.viscosityauto.ui.view.click.noMulClick
import com.hm.viscosityauto.utils.LimitUtil
import com.hm.viscosityauto.utils.SPUtils

import com.hm.viscosityauto.vm.CalibrationState
import com.hm.viscosityauto.vm.SettingVM
import com.hm.viscosityauto.vm.TestVM

/**
 * 温度校准 单点校准页面
 */
@Composable
fun CalibrationMulView(vm: SettingVM = viewModel()) {

    val context = LocalContext.current

    var pointTList = remember {
        mutableStateListOf<PointTModel>()
    }


    val multipleEditState = remember {
        mutableStateOf(false)
    }

    DisposableEffect(Unit) {
        vm.getLocalSetting()
        pointTList.clear()
        pointTList.addAll(vm.pointTList)
        onDispose { }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 42.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(id = R.string.temperature_edit_2),
            style = MaterialTheme.typography.titleMedium.copy(textColorBlue)
        )
        Spacer(modifier = Modifier.height(28.dp))

        //温度
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {

            Text(
                text = stringResource(id = R.string.set_temperature) + ":  ",
                style = MaterialTheme.typography.bodyLarge
            )

            InputView(value = vm.setTemperature, onValueChange = { vm.setTemperature = it})


            Spacer(modifier = Modifier.width(8.dp))

            if (vm.heatingState != 0) {
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
                            vm.stopTemperature()
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
                            if (LimitUtil.isOverLimit(context, vm.setTemperature)) {
                                return@noMulClick
                            }

                            vm.setTemperature(vm.setTemperature)
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
                text = vm.curTemperature + " ℃",
                style = MaterialTheme.typography.bodyLarge
            )
            if (vm.heatingState != 0) {
                Spacer(modifier = Modifier.width(8.dp))

                Image(
                    painter = painterResource(id = if (vm.heatingState == 1) R.mipmap.heating_icon else R.mipmap.keep_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                )
            }

        }

        Spacer(modifier = Modifier.height(24.dp))


        //tab
        Row(
            modifier = Modifier
                .width(380.dp)
                .height(40.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {


            Text(
                text = stringResource(id = R.string.raw_temperature),
                style = MaterialTheme.typography.bodyLarge
            )


            Text(
                text = stringResource(id = R.string.real_temperature),
                style = MaterialTheme.typography.bodyLarge
            )

        }


        LazyColumn(
            modifier = Modifier
                .width(380.dp)
                .height(200.dp),
        ) {
            itemsIndexed(pointTList) { index, items ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround

                ) {

                    InputView(value = items.testT, enabled = multipleEditState.value,onValueChange = {
                        pointTList[index] = pointTList[index].copy(testT = it)
                    })


                    InputView(value = items.realT,enabled = multipleEditState.value, onValueChange = {
                        pointTList[index] = pointTList[index].copy(realT = it)
                    })

                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            if (multipleEditState.value) {
                BaseButton(
                    title = stringResource(id = R.string.reset),
                ) {
                    multipleEditState.value = false
                    vm.pointTList = vm.pointTListDef
                    pointTList.clear()
                    pointTList.addAll(vm.pointTListDef)
                    SPUtils.getInstance()
                        .put("pointT", Gson().toJson(vm.pointTListDef.toList()))
                }
                Spacer(modifier = Modifier.width(20.dp))
                BaseButton(
                    title = stringResource(id = R.string.cancel),
                    isNegativeStyle = true
                ) {
                    multipleEditState.value = false
                }
                Spacer(modifier = Modifier.width(20.dp))
                BaseButton(title = stringResource(id = R.string.confirm)) {
                    val tempList: MutableList<PointTModel> = mutableStateListOf()
                    tempList.addAll(pointTList)
                    tempList.removeIf {
                        it == PointTModel("0.00", "0.00")
                    }

                    if (tempList.size < 2) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.test_data_shortage),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@BaseButton
                    }

                    val hasDuplicates = tempList.groupBy { it.testT }
                        .any { it.value.size > 1 }
                    if (hasDuplicates) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.have_duplicate_data),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@BaseButton
                    }

                    tempList.forEach {
                        if (LimitUtil.isOverLimit(
                                context,
                                it.testT
                            ) || LimitUtil.isOverLimit(context, it.realT)
                        ) {
                            return@BaseButton
                        }
                    }




                    multipleEditState.value = false
                    vm.pointTList = pointTList
                    SPUtils.getInstance().put("pointT", Gson().toJson(vm.pointTList))

                }
            } else {
                BaseButton(
                    title = stringResource(id = R.string.edit),
                ) {
                    multipleEditState.value = true
                }
            }

        }


    }


}


