package com.hm.viscosityauto.ui.view.click

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role


const val VIEW_CLICK_INTERVAL_TIME = 800//View的click方法的两次点击间隔时间

/**
 * 防止重复点击(有的人可能会手抖连点两次,造成奇怪的bug)
 */
@SuppressLint("ModifierFactoryUnreferencedReceiver")
@Composable
inline fun Modifier.noMulClick(
    time: Int = VIEW_CLICK_INTERVAL_TIME,
    enabled: Boolean = true,//中间这三个是clickable自带的参数
    onClickLabel: String? = null,
    role: Role? = null,
    crossinline onClick: () -> Unit
): Modifier {
    var lastClickTime by remember { mutableLongStateOf(value = 0L) }//使用remember函数记录上次点击的时间
    return clickable(enabled, onClickLabel, role) {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - time >= lastClickTime) {//判断点击间隔,如果在间隔内则不回调
            onClick()
            lastClickTime = currentTimeMillis
        }
    }
}