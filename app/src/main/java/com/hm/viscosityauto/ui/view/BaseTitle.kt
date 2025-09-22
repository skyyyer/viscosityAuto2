package com.hm.viscosityauto.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.theme.textColor

/**
 * 标题 基类
 */
@Preview()
@Composable
fun BaseTitle(
    title: String = "title",
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
                .fillMaxWidth()
                .height(58.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = R.mipmap.title_icon),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
//                textDecoration = TextDecoration.Underline,
            )

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