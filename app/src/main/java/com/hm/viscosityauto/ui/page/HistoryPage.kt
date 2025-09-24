package com.hm.viscosityauto.ui.page

import NoPressStateClick
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asi.nav.Nav
import com.google.gson.Gson
import com.hm.viscosityauto.R
import com.hm.viscosityauto.model.DurationModel
import com.hm.viscosityauto.room.test.TestRecords
import com.hm.viscosityauto.ui.theme.cardBg
import com.hm.viscosityauto.ui.theme.cardBgBlue
import com.hm.viscosityauto.ui.theme.keyBoardBg
import com.hm.viscosityauto.ui.theme.textColorBlue
import com.hm.viscosityauto.ui.theme.textColorGray
import com.hm.viscosityauto.ui.view.BaseButton
import com.hm.viscosityauto.ui.view.BaseDialog
import com.hm.viscosityauto.ui.view.BaseTitle
import com.hm.viscosityauto.ui.view.obtainFooterTipContent
import com.hm.viscosityauto.ui.view.obtainHeaderTipContent
import com.hm.viscosityauto.ui.view.obtainLastLoadTime
import com.hm.viscosityauto.ui.view.obtainLastRefreshTime
import com.hm.viscosityauto.vm.HistoryVM
import com.king.ultraswiperefresh.NestedScrollMode.*
import com.king.ultraswiperefresh.UltraSwipeRefresh
import com.king.ultraswiperefresh.indicator.classic.ClassicRefreshFooter
import com.king.ultraswiperefresh.indicator.classic.ClassicRefreshHeader
import com.king.ultraswiperefresh.rememberUltraSwipeRefreshState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HistoryPage(vm: HistoryVM = viewModel()) {
    val context = LocalContext.current


    var selIndex by remember {
        mutableIntStateOf(-1)
    }

    val delDialog = remember {
        mutableStateOf(false)
    }

    val state = rememberUltraSwipeRefreshState()

    val selList = remember {
        mutableStateListOf<TestRecords>()
    }

    val exportDialog = remember {
        mutableStateOf(false)
    }


    val selData = remember {
        mutableStateOf(TestRecords())
    }


    val openDurationDialog = remember {
        mutableStateOf(false)
    }


    LaunchedEffect(state.isRefreshing) {
        if (state.isRefreshing) {
            vm.recordsPage = 0
            selList.clear()
            this.launch(Dispatchers.IO) {
                delay(200)
                vm.getTestData()
                withContext(Dispatchers.Main) {
                    state.isRefreshing = false

                }
            }

        }

    }

    LaunchedEffect(state.isLoading) {
        if (state.isLoading) {
            vm.recordsPage++
            this.launch(Dispatchers.IO) {
                delay(200)
                vm.getTestData()
                withContext(Dispatchers.Main) {
                    state.isLoading = false
                }
            }

        }
    }

    DisposableEffect(Unit) {
        Log.e("HistoryPage", "DisposableEffect")
        if (vm.recordsList.size == 0) {
            state.isRefreshing = true
        }
        onDispose {

        }
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp)
        ) {
            //标题
            Box(modifier = Modifier.padding(horizontal = 28.dp)) {
                //标题
                BaseTitle(title = stringResource(id = R.string.test_records), onBack = {
                    Nav.back()
                })

                Row(
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 126.dp)
                ) {
                    if (selList.size == 1) {
                        BaseButton(
                            title = stringResource(id = R.string.print),
                            icon = R.mipmap.print_icon
                        ) {
                            val model = selList[0]
                            Log.e("data", Gson().toJson(model))
                            vm.printData(context, model)

                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    BaseButton(
                        title = stringResource(id = R.string.export),
                        icon = R.mipmap.export_icon
                    ) {

                        exportDialog.value = true
                    }

                    if (vm.admin.role == AdminRole.admin) {
                        Spacer(modifier = Modifier.width(20.dp))
                        BaseButton(
                            title = stringResource(id = R.string.del),
                            icon = R.mipmap.del_icon
                        ) {
                            delDialog.value = true
                        }
                    }
                }
            }

//            FunctionView(onSearch = { date, admin ->
//                vm.date = date
//                state.isRefreshing = true
//            }, onDel = {
//                delDialog.value = true
//            })
//

            Spacer(modifier = Modifier.height(20.dp))
            ItemRecordView(
                stringResource(id = R.string.number),
                stringResource(id = R.string.duration) + "(S)",
                stringResource(id = R.string.temperature_simple) + "(℃)",
//                stringResource(id = R.string.viscosity_constant) + "(mm²/S²)",
                stringResource(id = R.string.viscosity) + "(mm²/S)",
                stringResource(id = R.string.test_time),
                modifier = Modifier.background(
                    cardBg
                ),
                isTitle = true
            )

            UltraSwipeRefresh(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = state,
                onRefresh = { state.isRefreshing = true },
                onLoadMore = { state.isLoading = true },
                headerScrollMode = Translate,
                footerScrollMode = Translate,
                headerIndicator = {
                    ClassicRefreshHeader(
                        it,
                        tipContent = { obtainHeaderTipContent(it) },
                        tipTime = { obtainLastRefreshTime(it) })
                },
                footerIndicator = {
                    ClassicRefreshFooter(
                        it,
                        tipContent = { obtainFooterTipContent(it) },
                        tipTime = { obtainLastLoadTime(it) })
                }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    itemsIndexed(vm.recordsList) { index, items ->
                        ItemRecordView(items.testNum,
                            items.duration,
                            items.temperature,
//                            items.constant,
                            items.viscosity,
                            items.date + " " + items.time,
                            modifier = Modifier
                                .background(color = if (selIndex == index) keyBoardBg else Color.Transparent)
                                .clickable {
                                    if (items.durationArray.isNotEmpty()) {
                                        selData.value = items
                                        openDurationDialog.value = true
                                    }
                                }, onSel = {
                                if (selList.contains(items)) {
                                    selList.remove(items)
                                } else {
                                    selList.add(items)
                                }

                            }, isSel = selList.contains(items)
                        )
//                    }
                    }
                }

            }




        }


        //确认弹框
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
                    text = stringResource(id = if (selList.isEmpty()) R.string.del_all_tip else R.string.del_tip),
                    style = MaterialTheme.typography.bodyLarge.copy(color = textColorGray)
                )

                Spacer(modifier = Modifier.height(26.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    BaseButton(
                        title = stringResource(id = R.string.cancel),
                        isNegativeStyle = true
                    ) {
                        delDialog.value = false
                    }
                    Spacer(modifier = Modifier.width(16.dp))

                    BaseButton(title = stringResource(id = R.string.confirm)) {
                        delDialog.value = false
                        if (selList.isEmpty()) {
                            vm.delAllTestData()
                        } else {
                            vm.delTestDatas(selList)
                            selList.clear()
                        }
                    }

                }

            }


        }, dialogState = delDialog)


        //导出确认弹框
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
                    text = stringResource(id = if (selList.isEmpty()) R.string.export_all_tip else R.string.export_tip),
                    style = MaterialTheme.typography.bodyLarge.copy(color = textColorGray)
                )

                Spacer(modifier = Modifier.height(26.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    BaseButton(
                        title = stringResource(id = R.string.cancel),
                        isNegativeStyle = true
                    ) {
                        exportDialog.value = false
                    }
                    Spacer(modifier = Modifier.width(16.dp))

                    BaseButton(title = stringResource(id = R.string.confirm)) {
                        exportDialog.value = false

                        if (selList.isEmpty()) {
                            vm.exportDataAll(context)
                        } else {
                            vm.exportData(context, selList)
                        }
                    }

                }

            }

        }, dialogState = exportDialog)


        //时长列表
        if (openDurationDialog.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.1f))
                    .NoPressStateClick(onClick = {

                    }),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .requiredHeightIn(max = 600.dp)
                        .width(740.dp)
                        .defaultMinSize(minHeight = 280.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(5.dp))
                        .padding(6.dp),
                ) {

                    Image(
                        painter = painterResource(id = R.mipmap.close_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(26.dp)
                            .align(Alignment.TopEnd)
                            .clip(shape = RoundedCornerShape(13.dp))
                            .clickable {
                                openDurationDialog.value = false
                            }
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp, horizontal = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = stringResource(id = R.string.test_detail),
                            style = MaterialTheme.typography.titleMedium.copy(color = textColorBlue)
                        )
                        Spacer(modifier = Modifier.height(32.dp))

                        Row(Modifier.fillMaxWidth()) {
                            ItemDataView(
                                stringResource(id = R.string.number),
                                selData.value.testNum,
                                Modifier.weight(1f)
                            )
                            ItemDataView(
                                stringResource(id = R.string.viscosity_constant) + "(mm²/S²)",
                                selData.value.constant, Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(Modifier.fillMaxWidth()) {
                            ItemDataView(
                                stringResource(id = R.string.temperature) + "(℃)",
                                selData.value.temperature, Modifier.weight(1f)
                            )
                            ItemDataView(
                                stringResource(id = R.string.viscosity) + "(mm²/S)",
                                selData.value.viscosity, Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(Modifier.fillMaxWidth()) {
                            ItemDataView(
                                stringResource(id = R.string.tester),
                                selData.value.tester,
                                Modifier.weight(1f)
                            )
                            ItemDataView(
                                stringResource(id = R.string.duration) + "(S)",
                                selData.value.duration,
                                Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth()) {
                            ItemDataView(
                                stringResource(id = R.string.test_count), Gson().fromJson(
                                    selData.value.durationArray,
                                    Array<DurationModel>::class.java
                                ).size.toString(), Modifier.weight(1f)
                            )
                            ItemDataView(
                                stringResource(id = R.string.test_time),
                                selData.value.date + " " + selData.value.time,
                                Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = R.string.duration_list),
                            style = MaterialTheme.typography.bodyLarge.copy(color = textColorBlue),
                            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                            itemsIndexed(
                                Gson().fromJson(
                                    selData.value.durationArray,
                                    Array<DurationModel>::class.java
                                ).toList()
                            ) { index, items ->
                                ItemDurationView(index, items)
                            }

                        }
                    }
                }

            }
        }
    }
}

/**
 * 详情 数据
 */
@Composable
fun ItemDataView(
    title: String,
    value: String,
    modifier: Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$title:  ",
            style = MaterialTheme.typography.bodyLarge.copy(color = textColorBlue)
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge)

    }

}

@Composable
fun ItemRecordView(
    testNum: String,
    duration: String,
    temperature: String,
//    constant: String,
    viscosity: String,
    time: String,
    modifier: Modifier = Modifier,
    isTitle: Boolean = false,
    isSel: Boolean = false,
    onSel: (Boolean) -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(52.dp)
            .padding(horizontal = 32.dp)
            .fillMaxWidth()

    ) {

        if (!isTitle) {
            Checkbox(
                checked = isSel,
                onCheckedChange = {
                    onSel(it)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = cardBgBlue,
                    uncheckedColor = cardBgBlue
                ),
                modifier = Modifier.size(50.dp)
            )
        } else {
            Spacer(modifier = Modifier.width(50.dp))
        }


        Text(
            text = testNum,
            style = if (isTitle) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = duration,
            style = if (isTitle) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(0.8f)
        )

        Text(
            text = temperature,
            style = if (isTitle) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(0.6f)
        )

//
//        Text(
//            text = constant,
//            style = MaterialTheme.typography.titleSmall.copy(color = if (isTitle) textColorBlue else textColor),
//            modifier = Modifier.weight(1f)
//        )

        Text(
            text = viscosity,
            style = if (isTitle) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = time,
            style = if (isTitle) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1.2f)
        )
    }
}


