package com.hm.viscosityauto.ui.view

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hm.viscosityauto.R
import com.hm.viscosityauto.model.DurationModel
import com.hm.viscosityauto.model.PassageModel
import com.hm.viscosityauto.room.test.TestRecords
import com.hm.viscosityauto.ui.page.ItemDurationView
import com.hm.viscosityauto.ui.theme.TestCardBg
import com.hm.viscosityauto.ui.theme.cardBgBlue1
import com.hm.viscosityauto.ui.theme.cardBgGreen
import com.hm.viscosityauto.ui.theme.inputBgWhite
import com.hm.viscosityauto.ui.theme.keyBoardBg
import com.hm.viscosityauto.ui.theme.textColor
import com.hm.viscosityauto.ui.theme.textColorBlue
import com.hm.viscosityauto.ui.theme.textColorGray
import com.hm.viscosityauto.utils.SPUtils
import com.hm.viscosityauto.utils.ToastUtil
import com.hm.viscosityauto.vm.TestState
import com.hm.viscosityauto.vm.TestState.Clean
import com.hm.viscosityauto.vm.TestState.CleanEmpty
import com.hm.viscosityauto.vm.TestState.DecomP
import com.hm.viscosityauto.vm.TestState.Drying
import com.hm.viscosityauto.vm.TestState.Empty
import com.hm.viscosityauto.vm.TestState.Finish
import com.hm.viscosityauto.vm.TestState.FinishAll
import com.hm.viscosityauto.vm.TestState.Running
import com.hm.viscosityauto.vm.TestState.Start

@Composable
fun PassageCard(
    name: String = "A",
    model: PassageModel,
    timekeeping: String,
    keepTCount: String,
    dataOptShow: Boolean,
    onConfig: () -> Unit,
    onStart: () -> Unit,
    onFinish: () -> Unit,
    onConfirm: () -> Unit,
    onPrint: () -> Unit,
    updateModel: ((PassageModel) -> Unit) = {},
    onCompute: ((List<DurationModel>) -> Unit) = {},
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val durationShow = remember {
        mutableStateOf(dataOptShow)
    }

    val selList = remember {
        mutableStateListOf<Int>()
    }


    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
            .background(
                when (model.state) {
                    Empty -> {
                        TestCardBg
                    }

                    Running, Start -> {
                        cardBgBlue1
                    }

                    FinishAll -> {
                        cardBgGreen
                    }

                    else -> {
                        cardBgBlue1
                    }
                }, shape = RoundedCornerShape(5.dp)
            ),
        contentAlignment = Alignment.Center,
    ) {


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp, horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = if (name == "A") R.mipmap.a_icon else R.mipmap.b_icon),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    contentScale = ContentScale.Inside
                )
                Text(
                    text = when (model.state) {
                        Empty -> stringResource(id = R.string.empty)
                        Running, Start, Finish -> stringResource(id = R.string.testing)
                        Clean -> stringResource(id = R.string.clean)
                        CleanEmpty -> stringResource(id = R.string.clean_empty)
                        Drying -> stringResource(id = R.string.clean_drying)
                        DecomP -> stringResource(id = R.string.decomp)
                        FinishAll -> stringResource(id = R.string.finish)
                        else -> stringResource(id = R.string.empty)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                )

                Text(
                    text = stringResource(id = R.string.config),
                    style = MaterialTheme.typography.bodyLarge.copy(color = textColorBlue),
                    modifier = Modifier.clickable {
                        onConfig()
                    }
                )

            }



            Spacer(modifier = Modifier.height(20.dp))

            ItemData(
                name = stringResource(id = R.string.number),
                value = model.number,
                isOnlyNum = false,
                isEdit = model.state == 0,
                onInput = {
                    updateModel(model.copy(number = it))
                }
            )


            Spacer(modifier = Modifier.height(12.dp))


            ItemData(
                name = stringResource(id = R.string.viscosity_constant) + "(mm²/S²)",
                value = model.constant,
                isEdit = model.state == Empty,
                overflow = TextOverflow.Visible,
                onInput = {
                    updateModel(model.copy(constant = it))
                }
            )


            Spacer(modifier = Modifier.height(12.dp))

            ItemData(
                name = stringResource(id = R.string.test_count),
                value = if (model.state == Empty) model.testCount else "${model.curNum}  /  ${model.testCount}",
                isEdit = model.state == Empty,
                isClick = true,
                onInput = {
                    updateModel(model.copy(testCount = it))
                }, onClick = {
                    durationShow.value = true
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            ItemData(stringResource(id = R.string.keep_t_duration) + "(s)",
                if (model.state == Empty) model.keepTDuration else keepTCount,
                isEdit = model.state == Empty,
                onInput = {
                    updateModel(model.copy(keepTDuration = it))

                })


            Spacer(modifier = Modifier.height(12.dp))

            if (model.state == Empty) {

                ItemData(stringResource(id = R.string.extract_duration) + "(s)",
                    model.extractDuration,
                    isEdit = model.state == Empty,
                    onInput = {
                        updateModel(model.copy(extractDuration = it))

                    })


                Spacer(modifier = Modifier.height(12.dp))

                ItemData(stringResource(id = R.string.extract_interval) + "(s)",
                    model.extractInterval,
                    isEdit = model.state == Empty,
                    onInput = {
                        updateModel(model.copy(extractInterval = it))

                    })

                Spacer(modifier = Modifier.height(12.dp))

                if (SPUtils.getInstance().getBoolean("autoClean", true)) {
                    ItemData(stringResource(id = R.string.clean_times),
                        model.cleanTimes,
                        isEdit = model.state == Empty,
                        onInput = {
                            updateModel(model.copy(cleanTimes = it))

                        })

                    Spacer(modifier = Modifier.height(12.dp))

                    ItemData(stringResource(id = R.string.clean_duration) + "(s)",
                        model.cleanDuration,
                        isEdit = model.state == Empty,
                        onInput = {
                            updateModel(model.copy(cleanDuration = it))

                        })

                    Spacer(modifier = Modifier.height(12.dp))

                    ItemData(stringResource(id = R.string.add_liquid_duration) + "(s)",
                        model.addDuration,
                        isEdit = model.state == Empty,
                        onInput = {
                            updateModel(model.copy(addDuration = it))

                        })
                    Spacer(modifier = Modifier.height(12.dp))
                }

                ItemData(stringResource(id = R.string.motor_speed) + "(Kpa)",
                    model.motorSpeed,
                    isEdit = model.state == Empty,
                    onInput = {
                        updateModel(model.copy(motorSpeed = it))
                    })

                Spacer(modifier = Modifier.height(12.dp))
            } else {
                ItemData(stringResource(id = R.string.timekeeping), timekeeping, isTimer = true)

                Spacer(modifier = Modifier.height(12.dp))

                ItemData(stringResource(id = R.string.viscosity), model.viscosity)
            }


            Spacer(modifier = Modifier.weight(1f))

            Row {
                BaseButton(
                    title = stringResource(
                        id = when (model.state) {
                            Empty -> {
                                R.string.start

                            }

                            FinishAll -> {
                                R.string.finish
                            }

                            else -> {
                                R.string.end
                            }
                        }
                    )
                ) {
                    when (model.state) {
                        Empty -> {
                            if (model.isReady()) {
                                onStart()
                            } else {
                                ToastUtil.show(context, context.getString(R.string.input_error))
                            }
                        }

                        FinishAll -> {
                            onConfirm()
                        }

                        else -> {
                            onFinish()
                        }
                    }

                }

                if (model.state == FinishAll) {
                    Spacer(modifier = Modifier.width(16.dp))

                    BaseButton(
                        title = stringResource(
                            R.string.print
                        )
                    ) {
                        onPrint()
                    }
                }


            }

        }

        if (dataOptShow) {
            durationShow.value = false
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(5.dp))
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.duration_list),
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(model.durationArray) { index, items ->
                        ItemDurationView(
                            index,
                            items,
                            isEdit = true,
                            isSel = selList.contains(index),
                            onSel = {
                                if (selList.contains(index)) {
                                    selList.remove(index)
                                } else {
                                    selList.add(index)
                                }

                            })
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                BaseButton(title = stringResource(id = R.string.compute)) {
                    if (selList.size < 3) {
                        ToastUtil.show(context, context.getString(R.string.valid_data_is_less))
                        return@BaseButton
                    }
                    model.durationArray.forEachIndexed { index, item ->
                        item.derelict = !selList.contains(index)
                    }
                    onCompute(model.durationArray)
                    selList.clear()
                }
            }
        }



        if (durationShow.value) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(5.dp))
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.duration_list),
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(model.durationArray) { index, items ->
                        ItemDurationView(
                            index,
                            items,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                BaseButton(title = stringResource(id = R.string.close)) {
                    durationShow.value = false
                }
            }
        }

    }
}

@Composable
fun ItemData(
    name: String = "",
    value: String = "",
    isEdit: Boolean = false,
    isOnlyNum: Boolean = true,
    isClick: Boolean = false,
    isTimer: Boolean = false,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    onInput: (String) -> Unit = {},
    onClick: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current

    Row(
        modifier = if (isEdit || !isClick) Modifier.height(26.dp) else Modifier
            .height(26.dp)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$name: ",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1.2f),
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.width(16.dp))
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
                    .weight(1f)
                    .height(26.dp)
                    .background(color = inputBgWhite)
                    .wrapContentSize(Alignment.Center)
                    .padding(horizontal = 8.dp)
                    .onFocusChanged {
                    },
            )


        } else {
            if (isTimer) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = splitDecimalString(value).first,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = if (overflow == TextOverflow.Ellipsis) 1 else 2,
                        overflow = overflow,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = ".",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = splitDecimalString(value).second,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = if (overflow == TextOverflow.Ellipsis) 1 else 2,
                        overflow = overflow,
                        textAlign = TextAlign.Start
                    )
                }

            } else {
                Text(
                    modifier = Modifier
                        .weight(1f)
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

}

private fun splitDecimalString(input: String): Pair<String, String> {
    val parts = input.split(".")
    return when {
        parts.size == 1 -> parts[0] to "00" // 没有小数点
        parts.size >= 2 -> parts[0] to parts[1].take(2) // 取前两位小数
        else -> "" to "" // 无效输入
    }
}