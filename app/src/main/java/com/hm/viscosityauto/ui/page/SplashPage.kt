package com.hm.viscosityauto.ui.page

import android.content.Context
import android.content.pm.PackageInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asi.nav.Nav
import com.hm.viscosityauto.MainPageRoute
import com.hm.viscosityauto.R
import com.hm.viscosityauto.vm.MainVM
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@Composable
fun SplashPage(vm: MainVM = viewModel()) {
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {

        scope.launch {
            delay(200)

            vm.getLocalSetting()
        }


        onDispose {

        }
    }


    Box(modifier = Modifier.fillMaxSize().background(Color.Transparent)){

        Image(
            painter = painterResource(id = R.mipmap.home_bg),
            contentDescription = null,
            modifier = Modifier.size(446.dp,290.dp).align(Alignment.TopEnd)
        )
        Box(modifier = Modifier.size(40.dp, 40.dp).clickable {
            exitProcess(0)
        })


    }
}
