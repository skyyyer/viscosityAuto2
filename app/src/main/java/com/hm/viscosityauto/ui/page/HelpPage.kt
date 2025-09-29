package com.hm.viscosityauto.ui.page

import android.net.Uri
import android.provider.MediaStore.Video
import android.util.Log
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import com.asi.nav.Nav
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.view.BaseTitle
import com.hm.viscosityauto.utils.FileUtil
import io.sanghun.compose.video.RepeatMode
import io.sanghun.compose.video.VideoPlayer
import io.sanghun.compose.video.controller.VideoPlayerControllerConfig
import io.sanghun.compose.video.uri.VideoPlayerMediaItem

@Composable
fun HelpPage() {


    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 28.dp)
        ) {


            BaseTitle(title = stringResource(id = R.string.help), onBack = {
              Nav.back()
            })

            Spacer(modifier = Modifier.height(24.dp))

            Box (   modifier = Modifier
                .fillMaxWidth()
                .weight(1f)){
                VideoPlayer(
                    mediaItems = listOf(
                        VideoPlayerMediaItem.StorageMediaItem(
                            storageUri = FileUtil.FilePath2Uri("/sdcard/DCIM/1.mp4")
                        ),
                    ),
                    handleLifecycle = true,
                    autoPlay = true,
                    usePlayerController = true,
                    enablePip = true,
                    handleAudioFocus = true,
                    controllerConfig = VideoPlayerControllerConfig(
                        showSpeedAndPitchOverlay = false,
                        showSubtitleButton = false,
                        showCurrentTimeAndTotalTime = true,
                        showBufferingProgress = false,
                        showForwardIncrementButton = true,
                        showBackwardIncrementButton = true,
                        showBackTrackButton = false,
                        showNextTrackButton = false,
                        showRepeatModeButton = true,
                        controllerShowTimeMilliSeconds = 5_000,
                        controllerAutoShow = true, showFullScreenButton = false
                    ),
                    volume = 0.2f,  // volume 0.0f to 1.0f
                    repeatMode = RepeatMode.NONE,       // or RepeatMode.ALL, RepeatMode.ONE
                    onCurrentTimeChanged = { // long type, current player time (millisec)
                        Log.e("CurrentTime", it.toString())
                    },
                    playerInstance = { // ExoPlayer instance (Experimental)
                        addAnalyticsListener(
                            object : AnalyticsListener {
                                // player logger
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                )

            }
        }
    }



}