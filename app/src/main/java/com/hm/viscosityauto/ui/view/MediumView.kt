package com.hm.viscosityauto.ui.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.hm.viscosityauto.ui.theme.borderColor
import com.hm.viscosityauto.ui.theme.buttonEnd
import com.hm.viscosityauto.ui.theme.buttonStart
import com.hm.viscosityauto.ui.theme.cardBg
import com.hm.viscosityauto.ui.theme.cardBgBlue
import com.hm.viscosityauto.ui.theme.cardBgGray
import com.hm.viscosityauto.ui.theme.cardBgWhite
import com.hm.viscosityauto.ui.theme.dividerColor
import com.hm.viscosityauto.ui.theme.keyBoardBg
import com.hm.viscosityauto.ui.theme.textColor
import com.hm.viscosityauto.ui.theme.textColorBlue
import com.hm.viscosityauto.ui.theme.textColorGray
import com.hm.viscosityauto.ui.view.click.longClick
import com.hm.viscosityauto.ui.view.click.noMulClick
import com.hm.viscosityauto.utils.LimitUtil
import com.hm.viscosityauto.utils.SPUtils
import com.hm.viscosityauto.vm.SettingVM


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
            .background(if (isSle) cardBgBlue else cardBgWhite, shape = RoundedCornerShape(5.dp))
            .border( width = 1.dp,
                color = if (!isSle) borderColor else Color.Transparent,
                shape = RoundedCornerShape(5.dp))
            .longClick(onClick = {
                onClick()
            }, onLongClick = {
                onLongClick()
            })
            .padding(horizontal = 24.dp, vertical = 4.dp)
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
    model:MediumModel,
    onConfirm: (name: String, p: String) -> Unit,
    onCancel: () -> Unit,
    onDel: () -> Unit,
    onReset: () -> Unit,
    onDebug: (p: String) -> Unit,
    stopTemperature: () -> Unit,
    setT: (String) -> Unit,
) {

    val context = LocalContext.current

    var name by remember {
        mutableStateOf(model.name)
    }
    var p by remember {
        mutableStateOf(model.p)
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
                text = stringResource(id = if (model.name.isEmpty()) R.string.medium_add else R.string.medium_edit),
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
//                if (heatingState != 0) {
//                    Spacer(modifier = Modifier.width(8.dp))
//
//                    Image(
//                        painter = painterResource(id = if (heatingState == 1) R.mipmap.heating_icon else R.mipmap.keep_icon),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .size(20.dp)
//                    )
//                }

            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.medium_name),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(80.dp)
                )

                InputView(value = if (!model.isCanDel) {
                    if (name == "硅油")
                        context.getString(R.string.medium_silicone_oil) else context.getString(R.string.medium_water)
                } else name, width = 200.dp, enabled = model.isCanDel, onlyNum = false, onValueChange = {
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


                if (model.isCanDel&&model.name.isNotEmpty()){
                    BaseButton(
                        title = stringResource(id = R.string.del),
                    ) {
                        onDel()
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                }
                if (!model.isCanDel){
                    BaseButton(
                        title = stringResource(id = R.string.reset),
                    ) {
                        onReset()
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                }



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