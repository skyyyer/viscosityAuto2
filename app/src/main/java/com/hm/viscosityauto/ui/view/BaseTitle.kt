package com.hm.viscosityauto.ui.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.theme.buttonStart
import com.hm.viscosityauto.ui.theme.cardBgBlue
import com.hm.viscosityauto.ui.theme.textColor
import com.hm.viscosityauto.ui.theme.textEnd

/**
 * 标题 基类
 */
@Preview()
@Composable
fun BaseTitle(
    title: String = "标题",
    onBack: () -> Unit = {},
    onSubmit: () -> Unit = {},
    isShowBack: Boolean = true,
    rightText: String = ""
) {
    return Box(
        modifier = Modifier
            .fillMaxWidth(), contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = R.mipmap.title_icon),
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))



            val textLayoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }

            Box(Modifier.wrapContentSize()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    onTextLayout = { textLayoutResult.value = it }
                )

                textLayoutResult.value?.let { layoutResult ->
                    Canvas(modifier = Modifier.matchParentSize()) {
                        drawLine(
                            color = buttonStart,
                            start = Offset(0f, layoutResult.size.height - 2f),
                            end = Offset(layoutResult.size.width.toFloat(), layoutResult.size.height - 2f),
                            strokeWidth = 2f
                        )
                    }
                }
            }





            Spacer(modifier = Modifier.weight(1f))

            if (isShowBack) {
                BaseButton(
                    title = stringResource(id = R.string.back),
                    icon = R.mipmap.back_icon,
                    onClick = {
                        onBack()
                    })
            }

        }


    }
}

