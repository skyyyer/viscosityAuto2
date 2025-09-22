package com.hm.viscosityauto.ui.view.click

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.hm.viscosityauto.R
import com.king.ultraswiperefresh.UltraSwipeFooterState
import com.king.ultraswiperefresh.UltraSwipeHeaderState
import com.king.ultraswiperefresh.UltraSwipeRefreshState
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 根据[UltraSwipeRefreshState]获取提示内容
 */
@Composable
 fun obtainHeaderTipContent(state: UltraSwipeRefreshState): String {
    val textRes = when (state.headerState) {
        UltraSwipeHeaderState.PullDownToRefresh -> R.string.usr_pull_down_to_refresh
        UltraSwipeHeaderState.ReleaseToRefresh -> R.string.usr_release_to_refresh
        UltraSwipeHeaderState.Refreshing -> {
            if (state.isFinishing) {
                R.string.usr_refresh_completed
            } else {
                R.string.usr_refreshing
            }
        }
    }
    return stringResource(id = textRes)
}

/**
 * 获取上次刷新时间
 */
@Composable
 fun obtainLastRefreshTime(state: UltraSwipeRefreshState): String {
    var lastRefreshTime by remember("lastRefreshTime") {
        mutableLongStateOf(System.currentTimeMillis())
    }
    LaunchedEffect(state.headerState) {
        if (state.headerState == UltraSwipeHeaderState.Refreshing) {
            lastRefreshTime = System.currentTimeMillis()
        }
    }
    val context = LocalContext.current
    val dateFormat = remember {
        SimpleDateFormat(
           "MM-dd HH:mm",
            Locale.getDefault()
        )
    }
    return  context.getString(R.string.usr_last_refresh_time)+dateFormat.format(lastRefreshTime)
}


/**
 * 根据[UltraSwipeRefreshState]获取提示内容
 */
@Composable
 fun obtainFooterTipContent(state: UltraSwipeRefreshState): String {
    val textRes = when (state.footerState) {
        UltraSwipeFooterState.PullUpToLoad -> R.string.usr_pull_up_to_load
        UltraSwipeFooterState.ReleaseToLoad -> R.string.usr_release_to_load
        UltraSwipeFooterState.Loading -> {
            if (state.isFinishing) {
                R.string.usr_load_completed
            } else {
                R.string.usr_loading
            }
        }
    }
    return stringResource(id = textRes)
}


/**
 * 获取上次加载时间
 */
@Composable
 fun obtainLastLoadTime(state: UltraSwipeRefreshState): String {
    var lastLoadTime by remember("lastLoadTime") {
        mutableLongStateOf(System.currentTimeMillis())
    }
    LaunchedEffect(state.footerState) {
        if (state.footerState == UltraSwipeFooterState.Loading) {
            lastLoadTime = System.currentTimeMillis()
        }
    }
    val context = LocalContext.current
    val dateFormat = remember {
        SimpleDateFormat(
            "MM-dd HH:mm",
            Locale.getDefault()
        )
    }
    return  context.getString(R.string.usr_last_load_time)+dateFormat.format(lastLoadTime)
}