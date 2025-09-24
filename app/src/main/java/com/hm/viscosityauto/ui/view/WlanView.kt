package com.hm.viscosityauto.ui.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.theme.cardBgBlue
import com.hm.viscosityauto.ui.theme.cardBgGray
import com.hm.viscosityauto.ui.theme.cardBgWhite
import com.hm.viscosityauto.ui.theme.textColor
import com.hm.viscosityauto.ui.theme.textColorBlue
import com.hm.viscosityauto.utils.NetworkUtil
import com.iwdael.wifimanager.IWifi


@Composable
fun WlanView(
    state: Boolean,
    scanState: Int,
    connectedWifis: List<IWifi>,
    wifis: List<IWifi>,
    onStateChange: (Boolean) -> Unit,
    onConnect: (IWifi) -> Unit,
    onScan: () -> Unit,
    onClose:()->Unit,
) {

    val context = LocalContext.current


    Box(
        modifier = Modifier
            .size(733.dp, 442.dp)
            .shadow(
                elevation = 16.dp, shape = RoundedCornerShape(10.dp),
            )
            .background(color = cardBgWhite)
    ) {

        Image(
            painter = painterResource(id = R.mipmap.close_icon),
            contentDescription = null,
            modifier = Modifier.padding(end = 6.dp, top = 6.dp)
                .size(26.dp)
                .align(Alignment.TopEnd)
                .clip(shape = RoundedCornerShape(13.dp))
                .clickable {
                    onClose()
                }
        )
        Column {
            Row(   modifier = Modifier.fillMaxWidth().padding(top = 20.dp, start = 100.dp, end = 100.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(1.dp))


                Text(
                    text = stringResource(id = R.string.wifi),
                    style = MaterialTheme.typography.titleMedium.copy(textColorBlue)
                )


                Switch(modifier = Modifier
                    .height(20.dp),
                    checked = state,
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = cardBgBlue,
                        uncheckedThumbColor = Color.White,
                        uncheckedBorderColor = cardBgGray,
                        uncheckedTrackColor = cardBgGray
                    ),
                    onCheckedChange = {
                        onStateChange(it)
                    })


            }

            Spacer(modifier = Modifier.height(24.dp))


            HorizontalDivider(
                color = Color.Gray.copy(alpha = 0.5f),
                thickness = 1.dp,
            )

            if (state) {

                if (connectedWifis.isNotEmpty()) {
                    WifiItemView(connectedWifis[0], isConnected = true) {

                    }

                }

                LazyColumn(modifier = Modifier.weight(1f), content = {
                    itemsIndexed(wifis) { index, bean ->
                        WifiItemView(bean) {
                            onConnect(bean)
                        }
                    }
                })

                HorizontalDivider(
                    color = Color.Gray.copy(alpha = 0.5f),
                    thickness = 1.dp,
                )

                Box(
                    Modifier
                        .fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (scanState == 0) stringResource(id = R.string.scan) else stringResource(
                            id = R.string.scanning
                        ),
                        style = MaterialTheme.typography.titleSmall.copy(color = textColorBlue),
                        modifier = Modifier.clickable {
                            if (scanState == 0) {
                                onScan()
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.scanning),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }

            }


        }
    }
}

@Composable
fun WifiItemView(model: IWifi, isConnected: Boolean = false, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .height(62.dp)
            .clickable {
                onClick()
            }
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                // 绘制下划线（带 16dp 左右边距）
                drawLine(
                    color = cardBgGray.copy(alpha = 0.5f),
                    start = Offset(0f, size.height - strokeWidth / 2),
                    end = Offset(size.width, size.height - strokeWidth / 2),
                    strokeWidth = strokeWidth
                )


            }.padding(horizontal = 65.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        if (isConnected) {
            Box(
                Modifier
                    .width(60.dp)
                    .fillMaxHeight(), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.mipmap.selected_icon_blue),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    colorFilter = ColorFilter.tint(textColorBlue)
                )
            }
        } else {
            Spacer(modifier = Modifier.width(60.dp))
        }



        Text(

            text = model.name(),
            style = MaterialTheme.typography.bodyMedium.copy(color = if (isConnected) textColorBlue else textColor)
        )
        Spacer(modifier = Modifier.weight(1f))
        if (model.isEncrypt){
            Image(
                painter = painterResource(id = R.mipmap.lock_icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(30.dp))
        }

        Image(
            painter = painterResource(id = NetworkUtil.getWifiImage(model.level())),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
    }

}
