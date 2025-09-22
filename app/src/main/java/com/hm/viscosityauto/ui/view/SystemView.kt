package com.hm.viscosityauto.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aay.compose.baseComponents.model.GridOrientation
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType
import com.asi.nav.Nav
import com.hm.viscosityauto.ManagerPageRoute
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.theme.cardBg
import com.hm.viscosityauto.ui.theme.cardBgBlue
import com.hm.viscosityauto.ui.theme.cardBgGray
import com.hm.viscosityauto.ui.theme.cardBgWhite
import com.hm.viscosityauto.ui.theme.textColor
import com.hm.viscosityauto.ui.theme.textColorBlue
import com.hm.viscosityauto.ui.view.click.doubleClick
import com.hm.viscosityauto.utils.FileUtil
import com.hm.viscosityauto.vm.LANGUAGE_EN
import com.hm.viscosityauto.vm.LANGUAGE_ZH
import java.io.File


@Composable
fun SystemView(
    language: String,
    printState: Boolean,
    cleanState: Boolean,
    emptyState: Boolean,
    time: String,
    versionName: String,
    versionCode: Int,
    newApkPath: String,
    onLanguage: (String) -> Unit,
    onClean: (Boolean) -> Unit,
    onEmpty: (Boolean) -> Unit,
    onPrint: (Boolean) -> Unit,
    onTime: (Int, Int, Int, Int, Int, Int) -> Unit,
    onUpdate: () -> Unit
) {
    val timePickerDialog = remember {
        mutableStateOf(false)
    }

    val managerDialog = remember {
        mutableStateOf(false)
    }


    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Text(
            text = stringResource(id = R.string.language),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(22.dp))
        Row {
            Box(
                modifier = Modifier
                    .background(
                        if (language == LANGUAGE_EN) cardBgBlue else cardBgGray,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .weight(1f)
                    .height(50.dp)
                    .clickable {
                        onLanguage(LANGUAGE_EN)
                    }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.english),
                    style = MaterialTheme.typography.bodyMedium.copy(color = if (language == LANGUAGE_EN) Color.White else textColor)
                )
            }

            Spacer(modifier = Modifier.width(30.dp))
            Box(
                modifier = Modifier
                    .background(
                        if (language == LANGUAGE_ZH) cardBgBlue else cardBgGray,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .weight(1f)
                    .height(50.dp)
                    .clip(shape = RoundedCornerShape(4.dp))
                    .clickable {
                        onLanguage(LANGUAGE_ZH)
                    }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.chinese),
                    style = MaterialTheme.typography.bodyMedium.copy(color = if (language == LANGUAGE_ZH) Color.White else textColor)
                )
            }

        }

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = stringResource(id = R.string.time),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(22.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(cardBg, shape = RoundedCornerShape(6.dp))
                .clip(shape = RoundedCornerShape(6.dp))
                .clickable {
                    timePickerDialog.value = true
                }, contentAlignment = Alignment.Center
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Spacer(modifier = Modifier.height(50.dp))


        Text(
            text = stringResource(id = R.string.system),
            style = MaterialTheme.typography.bodyLarge
        )


        Spacer(modifier = Modifier.height(32.dp))

        Row {
//
//            Column {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Text(
//                        text = stringResource(id = R.string.auto_empty),
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                    Spacer(modifier = Modifier.width(20.dp))
//                    Switch(modifier = Modifier
//                        .height(24.dp),
//                        checked = emptyState,
//                        colors = SwitchDefaults.colors(
//                            checkedTrackColor = cardBgBlue,
//                            uncheckedThumbColor = Color.White,
//                            uncheckedBorderColor = cardBgGray,
//                            uncheckedTrackColor = cardBgGray
//                        ),
//                        onCheckedChange = {
//                            onEmpty(it)
//                        })
//
//
//                }
//
//                Spacer(modifier = Modifier.height(28.dp))
//
//
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Text(
//                        text = stringResource(id = R.string.auto_clean),
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                    Spacer(modifier = Modifier.width(20.dp))
//                    Switch(modifier = Modifier
//                        .height(24.dp),
//                        checked = cleanState,
//                        colors = SwitchDefaults.colors(
//                            checkedTrackColor = cardBgBlue,
//                            uncheckedThumbColor = Color.White,
//                            uncheckedBorderColor = cardBgGray,
//                            uncheckedTrackColor = cardBgGray
//                        ),
//                        onCheckedChange = {
//                            onClean(it)
//                        })
//
//
//                }
//
//            }
//
//            Spacer(modifier = Modifier.width(80.dp))
//
//
//            Column {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Text(
//                        text = stringResource(id = R.string.auto_print),
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                    Spacer(modifier = Modifier.width(20.dp))
//                    Switch(modifier = Modifier
//                        .height(24.dp),
//                        checked = printState,
//                        colors = SwitchDefaults.colors(
//                            checkedTrackColor = cardBgBlue,
//                            uncheckedThumbColor = Color.White,
//                            uncheckedBorderColor = cardBgGray,
//                            uncheckedTrackColor = cardBgGray
//                        ),
//                        onCheckedChange = {
//                            onPrint(it)
//                        })
//
//
//                }
//            }
//
//            Spacer(modifier = Modifier.width(160.dp))
//

            Column {
                Text(
                    text = "VERSION $versionName",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.doubleClick {
                        managerDialog.value = true
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))


                if (newApkPath.isNotEmpty()) {
                    if (File(newApkPath).exists() && FileUtil.extractVersionCodeFromApk(
                            context,
                            File(newApkPath)
                        ) > versionCode
                    ) {
                        BaseButton(isBrush = false, title = stringResource(id = R.string.update)) {
                            onUpdate()
                        }
                    }
                }

            }


        }

    }

    //时间选择
    BaseDialog(dialogState = timePickerDialog) {
        TimerPickerView(onConfirm = { year, month, day, hour, minute, second ->
            onTime(year, month, day, hour, minute, second)
            timePickerDialog.value = false
        }) {
            timePickerDialog.value = false
        }
    }

    //管理员弹窗
    BaseDialog(dialogState = managerDialog) {
        PwdDialogView(stringResource(id = R.string.manager_pwd),onCancel = {
            managerDialog.value = false
        }, onConfirm = {
            managerDialog.value = false
          if (it == "0523"){
              Nav.to(ManagerPageRoute.route)
          }
        })
    }
}



@Composable
fun PwdDialogView(
    title: String,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit
) {

    var pwd by remember {
        mutableStateOf("")
    }

    Box(
        modifier = Modifier
            .width(486.dp)
            .shadow(
                elevation = 16.dp, shape = RoundedCornerShape(10.dp),
            )
            .background(color = cardBgWhite.copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(
                color = Color.Gray.copy(alpha = 0.5f),
                thickness = 1.dp,
            )
            Spacer(modifier = Modifier.height(40.dp))



            ItemInputViewH(
                value = pwd,
                hint = stringResource(id = R.string.input_pwd),
                iconRes = R.mipmap.pwd_icon,
                onEdit = {
                    pwd = it
                })


            Spacer(modifier = Modifier.height(40.dp))

            HorizontalDivider(
                color = Color.Gray.copy(alpha = 0.5f),
                thickness = 1.dp,
            )

            Row(modifier = Modifier.height(54.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {
                            onCancel()
                        }, contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(id = R.string.cancel),
                        style = MaterialTheme.typography.titleSmall.copy(color = textColorBlue),
                        textAlign = TextAlign.Center
                    )
                }

                TabRowDefaults.Divider(
                    modifier = Modifier
                        .width(2.dp)
                        .height(30.dp),
                    color = Color.Gray.copy(alpha = 0.5f),
                    thickness = 1.dp,
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {
                            onConfirm(pwd)
                        }, contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(id = R.string.confirm),
                        style = MaterialTheme.typography.titleSmall.copy(color = textColorBlue),
                        textAlign = TextAlign.Center
                    )
                }
            }

        }

    }

}

