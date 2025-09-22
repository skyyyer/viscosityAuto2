package com.hm.viscosityauto.ui.view

import android.annotation.SuppressLint
import android.text.style.UnderlineSpan
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hm.viscosityauto.ui.theme.underLine


@Composable
fun ItemInputViewH(
    value: String,
    hint:String = "",
    iconRes:Int,
    onlyNumber: Boolean = false,
    @SuppressLint("ModifierParameter") modifier:Modifier = Modifier.size(388.dp,42.dp),
    singleLine:Boolean = true,
    onEdit: (String) -> Unit,
) {

    Box(
        modifier = modifier
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                // 绘制下划线（带 16dp 左右边距）
                drawLine(
                    color = underLine.copy(alpha = 0.5f),
                    start = Offset(16.dp.toPx(), size.height - strokeWidth / 2),
                    end = Offset(size.width - 16.dp.toPx(), size.height - strokeWidth / 2),
                    strokeWidth = strokeWidth
                )
            }
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(30.dp,35.dp),
                painter = painterResource(id = iconRes),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(16.dp))



            BasicTextField(
                value = value,
                textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                singleLine = singleLine,

                keyboardOptions = KeyboardOptions(keyboardType = if (onlyNumber) KeyboardType.Number else KeyboardType.Text),
                modifier = Modifier
                    .weight(1f)
                    .background(color = Color.Transparent)
                    .padding(end = 36.dp),
                onValueChange = {
                   onEdit(it)
                })
        }

    }
}