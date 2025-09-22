package com.hm.viscosityauto.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.theme.cardBgGray
import com.hm.viscosityauto.ui.theme.cardBgWhite
import com.hm.viscosityauto.utils.SPUtils

@Preview(widthDp = 1024, heightDp = 800)
@Composable
fun UploadView(
    uploadPath: String = "http://39.98.237.174:80/control/upload/data/uploadData",
    uploadName: String = "",
    uploadPwd: String ="",
    onSave: (String, String, String) -> Unit = {_,_,_->}
) {

    val context = LocalContext.current

    var name by remember {
        mutableStateOf(uploadName)
    }

    var pwd by remember {
        mutableStateOf(uploadPwd)
    }

    var path by remember {
        mutableStateOf(uploadPath)
    }


    var token by remember {
        mutableStateOf(SPUtils.getInstance().getString("token", "e4fec07c-8917-44ca-99f5-582daa869f02"))
    }



    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(id = R.string.upload_setting),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(100.dp))

        Row (verticalAlignment = Alignment.CenterVertically){
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


        Row (verticalAlignment = Alignment.CenterVertically){
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


        Row (verticalAlignment = Alignment.CenterVertically){
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
        Spacer(modifier = Modifier.height(20.dp))

        Row (verticalAlignment = Alignment.CenterVertically){
            Text(
                text = "token",
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
                    value = token ,
                    maxLines = 1,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .background(color = Color.Transparent)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    onValueChange = {
                        token = it
                        SPUtils.getInstance().put("token", "it")
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