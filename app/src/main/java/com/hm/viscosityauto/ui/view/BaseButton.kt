package com.hm.viscosityauto.ui.view

import android.annotation.SuppressLint
import android.widget.Button
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.theme.buttonBg
import com.hm.viscosityauto.ui.theme.buttonEnd
import com.hm.viscosityauto.ui.theme.buttonStart
import com.hm.viscosityauto.ui.theme.textColorGray
import com.hm.viscosityauto.ui.view.click.noMulClick

@Preview
@Composable
fun BaseButton(
    title: String = stringResource(id = R.string.ok),
    style: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        color = Color.White,
    ),
    containerColor: Color = buttonBg,
    isBrush: Boolean = true,
    isNegativeStyle: Boolean = false,
    isError: Boolean = false,
    icon: Int = 0,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .background(
                if (isError) {
                    Brush.verticalGradient(
                        listOf(
                            Color.Red,
                            Color.Red,
                        )
                    )
                } else if (isNegativeStyle) Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        Color.Transparent,
                    )
                ) else if (!isBrush) Brush.verticalGradient(
                    listOf(
                        containerColor,
                        containerColor,
                    )
                ) else {
                    Brush.verticalGradient(
                        listOf(
                            buttonStart,
                            buttonEnd,
                        )
                    )
                }, shape = RoundedCornerShape(5.dp)
            )
            .noMulClick {
                onClick()
            }
            .border(
                width = 1.dp,
                color = if (isNegativeStyle) textColorGray else Color.Transparent,
                shape = RoundedCornerShape(5.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        if (icon != 0) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            maxLines = 1,
            text = title,
            style = style.copy(color = if (isNegativeStyle) textColorGray else Color.White),
        )
    }
}