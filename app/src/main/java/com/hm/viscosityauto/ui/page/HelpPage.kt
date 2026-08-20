package com.hm.viscosityauto.ui.page

import NoPressStateClick
import android.net.Uri
import android.provider.MediaStore.Video
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import com.asi.nav.Nav
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.theme.textColorGray
import com.hm.viscosityauto.ui.view.BaseTitle
import com.hm.viscosityauto.utils.FileUtil
import com.hm.viscosityauto.utils.SPUtils
import com.hm.viscosityauto.vm.LANGUAGE_ZH
import com.hm.viscosityauto.vm.MainVM
import com.hm.viscosityped.utils.QRCodeUtil
import io.sanghun.compose.video.RepeatMode
import io.sanghun.compose.video.VideoPlayer
import io.sanghun.compose.video.controller.VideoPlayerControllerConfig
import io.sanghun.compose.video.uri.VideoPlayerMediaItem

@Composable
fun HelpPage(vm: MainVM) {
    val editModelDialog = remember {
        mutableStateOf(false)
    }


    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {


            BaseTitle(title = stringResource(id = R.string.help), onBack = {
                Nav.back()
            })

            Spacer(modifier = Modifier.weight(1f))
//
//            Box (   modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f)){
//                VideoPlayer(
//                    mediaItems = listOf(
//                        VideoPlayerMediaItem.StorageMediaItem(
//                            storageUri = FileUtil.FilePath2Uri(videoPath)
//                        ),
//                    ),
//                    handleLifecycle = true,
//                    autoPlay = true,
//                    usePlayerController = true,
//                    enablePip = true,
//                    handleAudioFocus = true,
//                    controllerConfig = VideoPlayerControllerConfig(
//                        showSpeedAndPitchOverlay = false,
//                        showSubtitleButton = false,
//                        showCurrentTimeAndTotalTime = true,
//                        showBufferingProgress = false,
//                        showForwardIncrementButton = true,
//                        showBackwardIncrementButton = true,
//                        showBackTrackButton = false,
//                        showNextTrackButton = false,
//                        showRepeatModeButton = true,
//                        controllerShowTimeMilliSeconds = 5_000,
//                        controllerAutoShow = true, showFullScreenButton = false
//                    ),
//                    volume = 0.2f,  // volume 0.0f to 1.0f
//                    repeatMode = RepeatMode.NONE,       // or RepeatMode.ALL, RepeatMode.ONE
//                    onCurrentTimeChanged = { // long type, current player time (millisec)
//                        Log.e("CurrentTime", it.toString())
//                    },
//                    playerInstance = { // ExoPlayer instance (Experimental)
//                        addAnalyticsListener(
//                            object : AnalyticsListener {
//                                // player logger
//                            }
//                        )
//                    },
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .align(Alignment.Center),
//                )
//
//            }


            QRCodeImage(
                model = vm.deviceModel.value,
                language = if(vm.language.value== LANGUAGE_ZH)"CN" else "EN",
                modifier = Modifier.size(250.dp).NoPressStateClick(onLongClick = {
                    editModelDialog.value = true
                })
            )
            Spacer(Modifier.height(16.dp))

            Text(
                stringResource(R.string.service_support_tip),
                style = MaterialTheme.typography.bodyLarge.copy(color = textColorGray),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))


        }

        if (editModelDialog.value){

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.1f))
                    .NoPressStateClick(onClick = {
                        editModelDialog.value = false
                    }),
                contentAlignment = Alignment.Center
            ) {
                ModelDialogView(vm.deviceModel.value, onCancel = {
                    editModelDialog.value = false
                }, onConfirm = {
                    editModelDialog.value = false

                    vm.deviceModel.value = it;
                    SPUtils.getInstance().put("deviceModel", it)
                })

            }
        }
    }



}

/**
 * 二维码显示组件
 */
@Composable
fun QRCodeImage(
    model: String,
    language:String,
    modifier: Modifier = Modifier,
    size: Int = 512,
    foregroundColor: Color = Color.Black,
    backgroundColor: Color = Color.White
) {
    val bitmap = remember(model) {
        QRCodeUtil.generate(
            model = model,
            language = language,
            size = size
        )
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "二维码",
            modifier = modifier
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Fit
        )
    } else {
        // 生成失败时的占位
        Box(
            modifier = modifier
                .background(Color.LightGray, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("生成失败", color = Color.Gray)
        }
    }
}