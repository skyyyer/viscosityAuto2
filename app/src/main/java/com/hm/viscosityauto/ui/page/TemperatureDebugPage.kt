package com.hm.viscosityauto.ui.page

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asi.nav.Nav
import com.google.gson.Gson
import com.hm.viscosityauto.MyApp
import com.hm.viscosityauto.R
import com.hm.viscosityauto.model.TemperatureModel
import com.hm.viscosityauto.ui.theme.GrayBg
import com.hm.viscosityauto.ui.theme.cardBgBlue
import com.hm.viscosityauto.ui.theme.cardBgGray
import com.hm.viscosityauto.ui.theme.cardBgWhite
import com.hm.viscosityauto.ui.theme.textColor
import com.hm.viscosityauto.ui.theme.textColorBlue
import com.hm.viscosityauto.ui.view.BaseButton
import com.hm.viscosityauto.ui.view.BaseTitle
import com.hm.viscosityauto.ui.view.LoadingDialog
import com.hm.viscosityauto.utils.ExportDataUtil
import com.hm.viscosityauto.utils.FileUtil
import com.hm.viscosityauto.utils.LimitUtil
import com.hm.viscosityauto.utils.TimeUtils
import com.hm.viscosityauto.utils.ToastUtil
import com.hm.viscosityauto.vm.MainVM
import com.hm.viscosityauto.vm.SettingVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Method


@Composable
fun TemperatureDebugPage(vm: SettingVM = viewModel()) {

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    var dur by remember {
        mutableStateOf("10")
    }

    val points = remember {
        mutableStateListOf("40")
    }


    var state by remember {
        mutableStateOf(false)
    }

    val devId = remember {
        if (Build.VERSION.SDK_INT >= 28) {
            try {
                val c = Class.forName("android.os.SystemProperties")
                val get: Method = c.getMethod("get", String::class.java)
                get.invoke(c, "ro.serialno") as String
            } catch (var4: Exception) {
                ""
            }
        } else {
            Build.SERIAL
        }
    }



    LaunchedEffect(state) {
        if (state) {
            vm.temperatureList.clear()
            var curPoint = points[0]

            // 每5秒记录一次当前温度、时间、加热状态
            val recordJob = launch {
                while (true) {
                    delay(5000)

                    vm.temperatureList.add(
                        TemperatureModel(
                            vm.curTemperature,
                            TimeUtils.timestampToString(),
                            vm.heatingState,
                            curPoint
                        )
                    )
                }
            }

            points.forEach { temperature ->

                Log.e("setTemperatureList", "开始设置温度: $temperature")
                curPoint = temperature
                // 1. 设置当前温度
                vm.setTemperature(temperature)
                delay(5000)
                // 2. 等待加热状态变为 2（带超时，防止死等）
                while (vm.heatingState != 2) {
                    delay(1000)
                }

                Log.e("setTemperatureList", "加热状态=2 已就绪，开始计时: $temperature")

                // 3. 计时一分钟
                delay(dur.toInt() * 60 * 1000L)

                Log.e("setTemperatureList", "计时完成: $temperature")
            }

            // 所有温度点执行完毕，停止记录
            recordJob.cancel()
            state = false
        }
    }


    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(vertical = 28.dp, horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //标题
            BaseTitle(title = stringResource(id = R.string.temperature_debug), onBack = {
                Nav.back()
            })

            Row(modifier = Modifier.padding(top = 16.dp)) {


                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(id = R.string.duration) + "(min):")
                        Spacer(modifier = Modifier.width(16.dp))

                        _InputView(value = dur, enabled = !state, onValueChange = {
                            dur = it
                        })

                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(id = R.string.temperature_point))
                        Spacer(modifier = Modifier.weight(1f))

                        BaseButton(
                            title = stringResource(id = R.string.add),
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color.White,
                            )
                        ) {
                            if (points.size >= 10||state) {
                                return@BaseButton
                            }
                            points.add("40")

                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        BaseButton(
                            title = stringResource(id = R.string.del),
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color.White,
                            )
                        ) {
                            if (points.size <= 1||state) {
                                return@BaseButton
                            }
                            points.removeLast()

                        }

                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        itemsIndexed(
                            points.toList()
                        ) { index, items ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = (index + 1).toString(),
                                    modifier = Modifier.width(32.dp)
                                )
                                _InputView(value = items,enabled = !state, onValueChange = {
                                    points[index] = it
                                })
                            }
                        }

                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    if (state) {
                        BaseButton(
                            title = stringResource(id = R.string.end),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                            )
                        ) {
                            vm.stopTemperature()
                            state = false
                        }
                    } else {
                        BaseButton(
                            title = stringResource(id = R.string.start),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                            )
                        ) {
                            if (dur.toIntOrNull()==null||dur.toInt()<0){
                                ToastUtil.show(context,context.getString(R.string.input_error))
                                return@BaseButton
                            }

                            points.forEach {
                                if (it.toFloatOrNull()==null){
                                    ToastUtil.show(context,context.getString(R.string.input_error))
                                    return@BaseButton
                                }
                                if (LimitUtil.isOverLimit(context,it)){
                                    return@BaseButton
                                }

                            }


                            state = true
                        }
                    }

                }

                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(color = GrayBg)
                )
                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(2f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        itemsIndexed(vm.temperatureList) { index, items ->
                            ItemTempView(
                                (index+1).toString(),
                                items.temperature,
                                items.state,
                                items.time,
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(24.dp))

                    BaseButton(
                        title = stringResource(id = R.string.export),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                        )
                    ) {

                        if (vm.temperatureList.isEmpty()) {
                            return@BaseButton
                        }
                        val path = FileUtil.getStoragePath(context, true)
                        Log.e("getStoragePath", path)

                        if (path.isEmpty()) {
                            Toast.makeText(
                                MyApp.getInstance(),
                                context.getText(R.string.export_fail_no_path),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@BaseButton
                        }

                        LoadingDialog.show(context.getString(R.string.exporting))

                        scope.launch(Dispatchers.IO) {
                            val success = ExportDataUtil().downloadTemperature(
                                context,
                                path,
                                devId,
                                vm.temperatureList.toList()
                            )

                            withContext(Dispatchers.Main) {
                                LoadingDialog.dismiss()
                                if (success) {
                                    Toast.makeText(
                                        MyApp.getInstance(),
                                        context.getText(R.string.export_success),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        MyApp.getInstance(),
                                        context.getText(R.string.export_fail),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        }

                    }
                }


            }


        }
    }
}


@Composable
fun ItemTempView(
    index: String,
    temperature: String,
    state: Int,
    time: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(52.dp)
            .padding(horizontal = 32.dp)
            .fillMaxWidth()
    ) {


        Text(
            text = index,
            style = MaterialTheme.typography.titleSmall.copy(color = textColor),
            modifier = Modifier.weight(0.5f)
        )

        Text(
            text = temperature,
            style = MaterialTheme.typography.titleSmall.copy(color = textColor),
            modifier = Modifier.weight(0.6f)
        )
        Text(
            text = when (state) {
                1 -> {
                    "加热"
                }

                2 -> {
                    "恒温"
                }

                else -> {
                    "空闲"
                }
            },
            style = MaterialTheme.typography.titleSmall.copy(color = textColor),
            modifier = Modifier.weight(0.6f)
        )

        Text(
            text = time,
            style = MaterialTheme.typography.titleSmall.copy(color = textColor),
            modifier = Modifier.weight(1.2f)
        )
    }
}


@Composable
fun _InputView(
    value: String,
    width: Dp = 90.dp,
    height: Dp = 36.dp,
    enabled: Boolean = true,
    onlyNum: Boolean = true,
    onValueChange: (String) -> Unit
) {
    // 记录文本是否超出可视区域，超出时改为左对齐，避免居中模式下首尾都看不见
    Box(
        modifier = Modifier
            .size(width, height)
            .border(
                width = 1.dp,
                color = cardBgGray,
                shape = RoundedCornerShape(5.dp)
            )
            .background(color = cardBgWhite, shape = RoundedCornerShape(5.dp)),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(keyboardType = if (onlyNum) KeyboardType.Number else KeyboardType.Text),
            singleLine = true,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Transparent)
                .wrapContentSize(Alignment.Center)
                .padding(horizontal = 8.dp),
            onValueChange = {
                onValueChange(it)
            })

    }

}