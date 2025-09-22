package com.hm.viscosityauto.ui.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.hm.viscosityauto.ui.theme.buttonStart
import com.hm.viscosityauto.ui.theme.cardBgGray

@Composable
fun CustomWidthSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    trackWidth: Dp = 38.dp, // 自定义轨道宽度
    trackHeight: Dp = 18.dp, // 自定义轨道高度
    thumbSize: Dp = 14.dp // 自定义滑块大小
) {
    // 计算滑块移动范围
    val maxSlideRange = trackWidth - thumbSize - (trackHeight - thumbSize)
    val animationProgress by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    Box(
        modifier = Modifier
            .width(trackWidth)
            .height(trackHeight)
            .clip(RoundedCornerShape(trackHeight))
            .clickable { onCheckedChange(!checked) }
    ) {
        // 绘制轨道
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = if (checked) buttonStart else cardBgGray,
                cornerRadius = CornerRadius(18f)
            )
        }

        // 绘制滑块
        Canvas(
            modifier = Modifier
                .size(thumbSize)
                .offset(
                    x = (trackHeight - thumbSize) / 2 + (animationProgress * maxSlideRange),
                    y = (trackHeight - thumbSize) / 2
                )
        ) {
            drawCircle(color = Color.White)
        }
    }
}