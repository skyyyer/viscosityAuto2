package com.hm.viscosityauto

import android.content.Intent
import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hm.viscosityauto.ui.theme.ViscosityAutoTheme


class ErrorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {
            ViscosityAutoTheme{
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {

                    val context = LocalContext.current

                    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.mipmap.power_icon),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(120.dp)
                                .clickable {
                                    val intent = Intent(context, MainActivity::class.java)
                                    startActivity(intent)
                                }
                        )

                        Spacer(modifier = Modifier.width(50.dp))

                        Image(
                            painter = painterResource(id = R.mipmap.exit_icon),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(120.dp)
                                .clickable {
                                    finish()
                                }
                        )

                    }
                }
            }
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        //隐藏状态栏
        val intent = Intent("show.systemui")
        sendBroadcast(intent)
        //禁止滑动唤出状态栏
        val intent2 = Intent("com.zc.open_gesture")
       sendBroadcast(intent2)
    }


}
