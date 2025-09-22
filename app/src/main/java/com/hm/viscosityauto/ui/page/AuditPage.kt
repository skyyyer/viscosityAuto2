package com.hm.viscosityauto.ui.page

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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asi.nav.Nav
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.theme.cardBg
import com.hm.viscosityauto.ui.theme.keyBoardBg
import com.hm.viscosityauto.ui.theme.textColor
import com.hm.viscosityauto.ui.theme.textColorBlue
import com.hm.viscosityauto.ui.theme.textColorGray
import com.hm.viscosityauto.ui.view.BaseButton
import com.hm.viscosityauto.ui.view.BaseDialog
import com.hm.viscosityauto.ui.view.BaseTitle
import com.hm.viscosityauto.ui.view.DatePickerView
import com.hm.viscosityauto.ui.view.obtainFooterTipContent
import com.hm.viscosityauto.ui.view.obtainHeaderTipContent
import com.hm.viscosityauto.ui.view.obtainLastLoadTime
import com.hm.viscosityauto.ui.view.obtainLastRefreshTime
import com.hm.viscosityauto.ui.view.AdminRole
import com.hm.viscosityauto.vm.AuditVM
import com.king.ultraswiperefresh.NestedScrollMode
import com.king.ultraswiperefresh.UltraSwipeRefresh
import com.king.ultraswiperefresh.indicator.classic.ClassicRefreshFooter
import com.king.ultraswiperefresh.indicator.classic.ClassicRefreshHeader
import com.king.ultraswiperefresh.rememberUltraSwipeRefreshState

@Composable
fun AuditPage(vm: AuditVM = viewModel()) {
    var selIndex by remember {
        mutableIntStateOf(-1)
    }

    val delDialog = remember {
        mutableStateOf(false)
    }

    val state = rememberUltraSwipeRefreshState()

    DisposableEffect(Unit) {
        vm.initDB()
        state.isRefreshing = true

        onDispose {
            // unregister(callback)
        }
    }


    LaunchedEffect(state.isRefreshing) {
        if (state.isRefreshing) {
            vm.recordsPage = 0
            vm.getTestData()
            state.isRefreshing = false
        }
    }

    LaunchedEffect(state.isLoading) {
        if (state.isLoading) {
            vm.recordsPage++
            vm.getTestData()
            state.isLoading = false
        }
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 28.dp)
        ) {
            //标题
            BaseTitle(title = stringResource(id = R.string.audit_history), onBack = {
                Nav.back()
            })



            Spacer(modifier = Modifier.height(24.dp))

            FunctionView(onSearch = { date, admin ->
                vm.date = date
                vm.admin = admin
                state.isRefreshing = true
            }, onDel = {
                delDialog.value = true
            })



            Spacer(modifier = Modifier.height(16.dp))

            ItemAuditView(
                stringResource(id = R.string.admin),
                stringResource(id = R.string.role),
                stringResource(id = R.string.content),
                stringResource(id = R.string.time),
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
                headerScrollMode = NestedScrollMode.Translate,
                footerScrollMode = NestedScrollMode.Translate,
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
                        ItemAuditView(items.user,
                            stringResource(id = if (items.role == AdminRole.admin) R.string.role_admin else R.string.role_user),
                            items.des,
                            "${items.date} ${items.time}",
                            modifier = Modifier
                                .background(color = if (selIndex == index) keyBoardBg else Color.Transparent)
                                .clickable {


                                })
                    }
                }
            }

        }


        //删除确认弹框
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
                    text = stringResource(id = if (selIndex == -1) R.string.del_all_tip else R.string.del_tip),
                    style = MaterialTheme.typography.bodyLarge.copy(color = textColorGray)
                )

                Spacer(modifier = Modifier.height(26.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    BaseButton(
                        title = stringResource(id = R.string.cancel),
                        isNegativeStyle = true
                    ) {
                        delDialog.value = false
                    }
                    Spacer(modifier = Modifier.width(16.dp))

                    BaseButton(title = stringResource(id = R.string.confirm)) {
                        delDialog.value = false
                        vm.delAllTestData()
                    }

                }

            }

        }, dialogState = delDialog)
    }

}


@Composable
fun ItemAuditView(
    admin: String,
    role: String,
    des: String,
    time: String,
    modifier: Modifier = Modifier,
    isTitle: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(52.dp)
            .padding(horizontal = 32.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = admin,
            style = MaterialTheme.typography.titleSmall.copy(
                color = if (isTitle) textColorBlue else textColor,
                fontSize = if (isTitle) 22.sp else 17.sp
            ),
            modifier = Modifier.weight(1f)
        )

        Text(
            text = role,
            style = MaterialTheme.typography.titleSmall.copy(
                color = if (isTitle) textColorBlue else textColor,
                fontSize = if (isTitle) 22.sp else 17.sp
            ),
            modifier = Modifier.weight(1f)
        )

        Text(
            text = des,
            style = MaterialTheme.typography.titleSmall.copy(
                color = if (isTitle) textColorBlue else textColor,
                fontSize = if (isTitle) 22.sp else 17.sp
            ),
            modifier = Modifier.weight(1f)
        )

        Text(
            text = time,
            style = MaterialTheme.typography.titleSmall.copy(
                color = if (isTitle) textColorBlue else textColor,
                fontSize = if (isTitle) 22.sp else 17.sp
            ),
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
fun FunctionView(onSearch: (String, String) -> Unit, onDel: () -> Unit) {
    val allStr = stringResource(id = R.string.all)
    val date = remember {
        mutableStateOf("")
    }

    val admin = remember {
        mutableStateOf("")
    }


    val adminDialog = remember {
        mutableStateOf(false)
    }
    val dateDialog = remember {
        mutableStateOf(false)
    }


    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

        Text(text = stringResource(id = R.string.date))
        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(200.dp, 40.dp)
                .background(color = cardBg, shape = RoundedCornerShape(2.dp))
                .clickable {
                    dateDialog.value = true
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.value.ifEmpty { stringResource(id = R.string.all) }, modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium.copy(textColorBlue),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.width(32.dp))


        Text(text = stringResource(id = R.string.admin))
        Spacer(modifier = Modifier.width(8.dp))


        Box(
            modifier = Modifier
                .size(120.dp, 40.dp)
                .background(color = cardBg, shape = RoundedCornerShape(2.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = admin.value.ifEmpty { stringResource(id = R.string.all) }, modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium.copy(textColorBlue),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        BaseButton(stringResource(id = R.string.reset)) {
            date.value = ""
            admin.value = ""
            onSearch(date.value,admin.value)
        }


        Spacer(modifier = Modifier.weight(1f))

        BaseButton(stringResource(id = R.string.del_all)){
            onDel()
        }

        Spacer(modifier = Modifier.width(16.dp))

    }


    //时间选择弹窗
    BaseDialog(dialogState = dateDialog) {
        DatePickerView(onConfirm = { year, mon, day ->
            date.value = "$year-$mon-$day"
            dateDialog.value = false
            onSearch(date.value,admin.value)
        }, onCancel = {
            dateDialog.value = false
        })

    }


//    //用户选择弹窗
//    BaseDialog(dialogState = adminDialog) {
//        AdminAddView(selAdmin.name, selAdmin.pwd, selAdmin.role) { name, pwd, role ->
//            editAdmin(selAdmin.copy(name = name, pwd = pwd, role = role))
//            editAdminDialog.value = false
//        }
//    }


}