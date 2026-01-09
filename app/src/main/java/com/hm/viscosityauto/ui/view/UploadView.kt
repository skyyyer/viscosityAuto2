package com.hm.viscosityauto.ui.view

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.theme.cardBgGray
import com.hm.viscosityauto.ui.theme.cardBgWhite
import com.hm.viscosityauto.ui.theme.textColorBlue
import com.hm.viscosityauto.utils.SPUtils
import java.lang.reflect.Method

@Preview()
@Composable
fun UploadView(
    uploadPath: String = "http://39.98.237.174:80/control/upload/data/uploadData",
    uploadName: String = "",
    uploadPwd: String = "",
    deviceId: String = "",
    onSave: (String, String, String) -> Unit = { _, _, _ -> },
    onClose: () -> Unit = {}
) {

    var name by remember {
        mutableStateOf(uploadName)
    }

    var pwd by remember {
        mutableStateOf(uploadPwd)
    }

    var path by remember {
        mutableStateOf(uploadPath)
    }




    Box(
        modifier = Modifier
            .shadow(
                elevation = 16.dp, shape = RoundedCornerShape(10.dp),
            )
            .background(color = cardBgWhite)
    ) {

        Image(
            painter = painterResource(id = R.mipmap.close_icon),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 6.dp, top = 6.dp)
                .size(26.dp)
                .align(Alignment.TopEnd)
                .clip(shape = RoundedCornerShape(13.dp))
                .clickable {
                    onClose()
                }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(20.dp),
        ) {


            Text(
                text = stringResource(id = R.string.upload_setting),
                style = MaterialTheme.typography.titleMedium.copy(textColorBlue)
            )

            Spacer(modifier = Modifier.height(24.dp))



            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.device_id),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(120.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = deviceId,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(400.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.upload_path),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(120.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .size(400.dp, 46.dp)
                        .border(
                            width = 1.dp,
                            color = cardBgGray,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .background(color = cardBgWhite),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = path,
                        maxLines = 1,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .background(color = Color.Transparent)
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        onValueChange = {
                            path = it
                        })
                }
            }
            Spacer(modifier = Modifier.height(20.dp))


            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.upload_user),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(120.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .size(400.dp, 46.dp)
                        .border(
                            width = 1.dp,
                            color = cardBgGray,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .background(color = cardBgWhite),
                    contentAlignment = Alignment.CenterStart

                ) {
                    BasicTextField(
                        value = name,
                        maxLines = 1,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .background(color = Color.Transparent)
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        onValueChange = {
                            name = it
                        })
                }
            }

            Spacer(modifier = Modifier.height(20.dp))


            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.upload_pwd),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(120.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .size(400.dp, 46.dp)
                        .border(
                            width = 1.dp,
                            color = cardBgGray,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .background(color = cardBgWhite),
                    contentAlignment = Alignment.CenterStart

                ) {
                    BasicTextField(
                        value = pwd,
                        maxLines = 1,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .background(color = Color.Transparent)
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        onValueChange = {
                            pwd = it
                        })
                }
            }


            Spacer(modifier = Modifier.height(24.dp))

            BaseButton(
                stringResource(id = R.string.save),
                style = MaterialTheme.typography.titleSmall
            ) {
                onSave(path, name, pwd)
            }
        }
    }

}