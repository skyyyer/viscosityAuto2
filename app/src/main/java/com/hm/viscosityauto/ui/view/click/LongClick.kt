package com.hm.viscosityauto.ui.view.click

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput


/**
 * 双击事件
 */
fun Modifier.longClick(onLongClick: (Offset) -> Unit,onClick:(Offset)->Unit): Modifier =
    //处理手势反馈
    pointerInput(this) {
        //处理基础手势反馈
        detectTapGestures(
//            onDoubleTap = onDoubleClick//双击时回调
//            onPress = {}//按下时回调
            onLongPress = onLongClick,//长按时回调
            onTap = onClick//轻触时回调(按下并抬起)
        )


    }