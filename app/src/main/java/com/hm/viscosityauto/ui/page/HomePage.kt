package com.hm.viscosityauto.ui.page

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hm.viscosityauto.R
import com.hm.viscosityauto.vm.MainVM
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.asi.nav.Nav
import com.hm.viscosityauto.AuditPageRoute
import com.hm.viscosityauto.HelpPageRoute
import com.hm.viscosityauto.HistoryPageRoute
import com.hm.viscosityauto.SettingPageRoute
import com.hm.viscosityauto.TestPageRoute
import com.hm.viscosityauto.ui.theme.cardBg
import com.hm.viscosityauto.ui.theme.cardBgGray1
import com.hm.viscosityauto.ui.theme.textEnd
import com.hm.viscosityauto.ui.theme.textStart
import com.hm.viscosityauto.utils.FileUtil

@Composable
fun HomePage(vm: MainVM = viewModel()) {

    val context = LocalContext.current




    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Image(
            painter = painterResource(id = R.mipmap.home_bg),
            contentDescription = null,
            modifier = Modifier.size(446.dp,290.dp).align(Alignment.TopEnd)
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
        ) {

            Spacer(modifier = Modifier.height(30.dp))

            TimeView(vm.week, vm.date, vm.time, vm.adminInfo.value.name)

            //标题
            Spacer(modifier = Modifier.height(50.dp))

            Image(
                painter = painterResource(id = R.mipmap.app_title_icon),
                contentDescription = null,
                modifier = Modifier
                    .height(68.dp)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Inside
            )

            Spacer(modifier = Modifier.height(50.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                ItemCard(stringResource(id = R.string.test), R.mipmap.test_icon, bg = cardBgGray1.copy(alpha = 0.25f)) {
                    Nav.to(TestPageRoute.route)
//                    Log.e("点击",System.currentTimeMillis().toString())
//                    Nav.to(TestTypePageRoute.route)
                }
                Spacer(modifier = Modifier.width(18.dp))
                ItemCard(stringResource(id = R.string.test_records), R.mipmap.history_icon) {
                    Nav.to(HistoryPageRoute.route)
                }
                Spacer(modifier = Modifier.width(18.dp))
                ItemCard(stringResource(id = R.string.setting), R.mipmap.setting_icon, bg = cardBgGray1.copy(alpha = 0.25f)) {
                    Nav.to(SettingPageRoute.route)
                }
                Spacer(modifier = Modifier.width(18.dp))
                ItemCard(stringResource(id = R.string.help), R.mipmap.help_icon) {
                    if (FileUtil.FilePath2Uri(vm.helpVideoPath) == Uri.EMPTY) {
                        Toast.makeText(
                            context, context.getString(R.string.file_no_found),
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        Nav.to(HelpPageRoute.route)
                    }


                }

            }


            Box {

            }


        }

    }


}


@Composable
fun TimeView(week: String, date: String, time: String, admin: String) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0XFF0074F5),
                            Color(0XFF71B7F4),
                        )
                    )
                    , shape = RoundedCornerShape(20.dp)
                ), contentAlignment = Alignment.Center
        ){
            Image(
                painter = painterResource(id = R.mipmap.manager_icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp,24.dp)
            )
        }
        Spacer(modifier = Modifier.width(18.dp))
        Text(
            text = admin,
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 26.sp),
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(horizontalAlignment = Alignment.End) {
            Text(text = week, style = TextStyle(fontSize = 14.sp))
            Text(text = date, style = TextStyle(fontSize = 14.sp))
        }
        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = time,
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 35.sp),
            modifier = Modifier.width(150.dp)
        )
    }
}

@Composable
private fun ItemCard(title: String, res: Int,bg:Color =cardBg.copy(alpha = 0.8f),  onClick: (() -> Unit) = {}) {
    Box(modifier = Modifier
        .size(212.dp, 312.dp)
        .background(bg,shape = RoundedCornerShape(15.dp))
        .clip(shape = RoundedCornerShape(15.dp))
        .clickable {
            onClick()
        }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            Image(
                painter = painterResource(id = res),
                contentDescription = null,
                modifier = Modifier.size(82.dp)
            )

            Spacer(modifier = Modifier.height(70.dp))


            Text(text = title, style = MaterialTheme.typography.titleLarge)
        }


    }
}