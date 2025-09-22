package com.hm.viscosityauto.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hm.viscosityauto.ui.theme.cardBgGray
import com.hm.viscosityauto.ui.theme.cardBgWhite


@Composable
fun ItemInputView(
    title: String,
    value: String,
    onlyNumber: Boolean = false,
    onEdit: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .width(360.dp)
            .padding(horizontal = 32.dp),
    ) {

        Text(text = title, modifier = Modifier.width(150.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(360.dp, 46.dp)
                .border(
                    width = 1.dp,
                    color = cardBgGray,
                    shape = RoundedCornerShape(5.dp)
                )
                .background(color = cardBgWhite),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                textStyle = MaterialTheme.typography.bodyMedium,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = if (onlyNumber) KeyboardType.Number else KeyboardType.Text),
                modifier = Modifier
                    .background(color = Color.Transparent)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .fillMaxWidth(),
                onValueChange = {
                    onEdit(it)
                })

        }
    }
}