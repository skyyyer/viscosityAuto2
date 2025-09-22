package com.hm.viscosityauto.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.theme.cardBgWhite
import com.hm.viscosityauto.ui.theme.textColorGray

@Preview
@Composable
fun BaseDialogContent(
    title: String = "提示",
    content: String = "提示内容",
    onCancel: () -> Unit= {},
    onConfirm: () -> Unit = {},
) {

    Box(
        modifier = Modifier
            .width(415.dp)
            .shadow(
                elevation = 16.dp, shape = RoundedCornerShape(10.dp),
            )
            .background(color = cardBgWhite)
    ) {

        Column(
            modifier = Modifier
                .width(440.dp)
                .background(color = Color.White, shape = RoundedCornerShape(5.dp))
                .padding(30.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge.copy(color = textColorGray)
            )

            Spacer(modifier = Modifier.height(26.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                BaseButton(
                    title = stringResource(id = R.string.cancel),
                    isNegativeStyle = true
                ) {
                    onCancel()
                }
                Spacer(modifier = Modifier.width(16.dp))

                BaseButton(title = stringResource(id = R.string.confirm)) {
                    onConfirm()
                }

            }

        }
    }
}