package com.hm.viscosityauto.ui.page

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asi.nav.Nav
import com.hm.viscosityauto.MainPageRoute
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.theme.GrayBg
import com.hm.viscosityauto.ui.theme.cardBg
import com.hm.viscosityauto.ui.theme.cardBgBlue
import com.hm.viscosityauto.ui.theme.cardBgGray
import com.hm.viscosityauto.ui.theme.cardBgWhite
import com.hm.viscosityauto.ui.theme.inputBgWhite
import com.hm.viscosityauto.ui.theme.textColorBlue
import com.hm.viscosityauto.ui.theme.textEnd
import com.hm.viscosityauto.ui.theme.textStart
import com.hm.viscosityauto.ui.view.BaseButton
import com.hm.viscosityauto.utils.SPUtils
import com.hm.viscosityauto.vm.MainVM

@Composable
fun LoginPage(vm: MainVM = viewModel()) {

    val userName = remember {
        mutableStateOf(vm.loginInfo.name)
    }

    val pwd = remember {
        mutableStateOf(vm.loginInfo.pwd)
    }

    val auto = remember {
        mutableStateOf(vm.loginInfo.pwd.isNotEmpty())
    }

    val context = LocalContext.current

    Box {
        Image(
            painter = painterResource(id = R.mipmap.home_bg),
            contentDescription = null,
            modifier = Modifier.size(446.dp,290.dp).align(Alignment.TopEnd)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            //标题
            Spacer(modifier = Modifier.height(96.dp))

            Image(
                painter = painterResource(id = R.mipmap.app_title_icon),
                contentDescription = null,
                modifier = Modifier
                    .height(68.dp)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Inside
            )

//            Text(
//                text = stringResource(id = R.string.app_name),
//                style = MaterialTheme.typography.displayLarge.copy(
//                    brush = Brush.verticalGradient(
//                        colors = arrayListOf(
//                            textStart, textEnd
//                        )
//                    ), fontWeight = FontWeight.Bold
//                ),
//                modifier = Modifier.fillMaxWidth(),
//                textAlign = TextAlign.Center
//            )

            Spacer(modifier = Modifier.height(44.dp))

            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .size(415.dp, 348.dp)
                    .shadow(
                        elevation = 16.dp, shape = RoundedCornerShape(10.dp),
                    )
                    .background(color = cardBgWhite)

                    .align(Alignment.CenterHorizontally)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = stringResource(id = R.string.user_login),
                        style = MaterialTheme.typography.titleMedium.copy(color = textColorBlue)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Box(
                        modifier = Modifier
                            .size(324.dp, 46.dp)
                            .border(
                                width = 1.dp,
                                color = cardBgGray,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .background(color = cardBgWhite)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 14.dp)
                                .fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(id = R.mipmap.user_icon),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(16.dp))


                            BasicTextField(
                                value = userName.value,
                                textStyle = MaterialTheme.typography.bodyMedium,
                                singleLine = true,
                                modifier = Modifier
                                    .background(color = Color.Transparent)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                onValueChange = {
                                    userName.value = it
                                })
                        }

                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(324.dp, 46.dp)
                            .border(
                                width = 1.dp,
                                color = cardBgGray,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .background(color = cardBgWhite)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 14.dp)
                                .fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(id = R.mipmap.pwd_icon),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            BasicTextField(
                                value = pwd.value,
                                textStyle = MaterialTheme.typography.bodyMedium,
                                singleLine = true,
                                modifier = Modifier
                                    .background(color = Color.Transparent)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                onValueChange = {
                                    pwd.value = it
                                })
                        }

                    }

                    Spacer(modifier = Modifier.height(16.dp))


                    Box(
                        modifier = Modifier
                            .width(308.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(
                                        shape = RoundedCornerShape(2.dp),
                                        color = if (auto.value) cardBgBlue else cardBgGray
                                    )
                                    .clickable {
                                        auto.value = !auto.value
                                    }, contentAlignment = Alignment.Center
                            ) {
                                if (auto.value) {
                                    Image(
                                        modifier = Modifier.size(10.dp),
                                        painter = painterResource(id = R.mipmap.selected_icon),
                                        contentScale = ContentScale.FillHeight,
                                        contentDescription = null
                                    )
                                }

                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                stringResource(id = R.string.auto_login),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    BaseButton(
                        stringResource(id = R.string.login),
                        modifier = Modifier
                            .size(308.dp, 50.dp),
                        style = MaterialTheme.typography.titleSmall.copy(color = Color.White)
                    ) {

                        vm.adminLogin(context, userName.value, pwd.value, auto.value)


                    }

                    Spacer(modifier = Modifier.height(44.dp))

                }

            }
        }
    }

}

