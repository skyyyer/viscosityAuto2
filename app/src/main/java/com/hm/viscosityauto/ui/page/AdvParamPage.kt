package com.hm.viscosityauto.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asi.nav.Nav
import com.google.gson.Gson
import com.hm.viscosityauto.R
import com.hm.viscosityauto.model.AdvParamModel
import com.hm.viscosityauto.ui.theme.cardBg
import com.hm.viscosityauto.ui.theme.cardBgGray
import com.hm.viscosityauto.ui.theme.cardBgWhite
import com.hm.viscosityauto.ui.theme.dividerColor
import com.hm.viscosityauto.ui.theme.keyBoardBg
import com.hm.viscosityauto.ui.view.BaseButton
import com.hm.viscosityauto.ui.view.BaseTitle
import com.hm.viscosityauto.utils.SPUtils
import com.hm.viscosityauto.utils.ToastUtil
import com.hm.viscosityauto.vm.SettingVM
import kotlinx.coroutines.launch


@Composable
fun AdvParamPage(vm: SettingVM = viewModel()) {

    val context = LocalContext.current

    val scope  = rememberCoroutineScope()

    var advParamModel by remember {
        mutableStateOf(
            Gson().fromJson(
                SPUtils.getInstance().getString("advParamModel", Gson().toJson(AdvParamModel())),
                AdvParamModel::class.java
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 28.dp)) {
            BaseTitle(title = stringResource(id = R.string.advanced_param), onBack = {
                Nav.back()
            })
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column (modifier = Modifier.padding(horizontal = 40.dp)){

            Row (verticalAlignment = Alignment.CenterVertically){
                Image(painter = painterResource(id = R.mipmap.empty_set_icon), contentDescription = null,
                    modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(id = R.string.clean_empty_setting),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row (modifier = Modifier.padding(horizontal = 32.dp)){
                Text(
                    text = stringResource(id = R.string.motor_speed)+"(Kpa)",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))

                InputView( value = advParamModel.emptySpeed, onValueChange = {
                    advParamModel = advParamModel.copy(emptySpeed = it)
                })

                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    text = stringResource(id = R.string.extract_duration)+"(s)",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))

                InputView( value = advParamModel.emptyExtractDuration, onValueChange = {
                    advParamModel = advParamModel.copy(emptyExtractDuration = it)
                })

                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    text = stringResource(id = R.string.extract_interval)+"(s)",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))

                InputView( value = advParamModel.emptyExtractInterval, onValueChange = {
                    advParamModel = advParamModel.copy(emptyExtractInterval = it)
                })

                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    text = stringResource(id = R.string.drying_duration)+"(s)",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))

                InputView( value = advParamModel.emptyDryingDuration, onValueChange = {
                    advParamModel = advParamModel.copy(emptyDryingDuration = it)
                })

            }
            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(thickness = 1.dp, color = dividerColor, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(24.dp))

            Row (verticalAlignment = Alignment.CenterVertically){
                Image(painter = painterResource(id = R.mipmap.clean_set_icon), contentDescription = null,
                    modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(id = R.string.clean_setting),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row  (modifier = Modifier.padding(horizontal = 32.dp)){
                Text(
                    text = stringResource(id = R.string.motor_speed)+"(Kpa)",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))

                InputView( value = advParamModel.cleanSpeed, onValueChange = {
                    advParamModel = advParamModel.copy(cleanSpeed = it)
                })

                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    text = stringResource(id = R.string.drying_duration)+"(s)",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))

                InputView( value = advParamModel.cleanDryingDuration, onValueChange = {
                    advParamModel = advParamModel.copy(cleanDryingDuration = it)
                })

            }


            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(thickness = 1.dp, color = dividerColor, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(24.dp))


            Row (verticalAlignment = Alignment.CenterVertically){
                Image(painter = painterResource(id = R.mipmap.press_set_icon), contentDescription = null,
                    modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(id = R.string.decomp_setting),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row  (modifier = Modifier.padding(horizontal = 32.dp)){
                Text(
                    text = stringResource(id = R.string.decomp_duration)+"(s)",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))

                InputView( value = advParamModel.decompDuration, onValueChange = {
                    advParamModel = advParamModel.copy(decompDuration = it)
                })

            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(thickness = 1.dp, color = dividerColor, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(24.dp))


            Row (verticalAlignment = Alignment.CenterVertically){
                Image(painter = painterResource(id = R.mipmap.heat_set_icon), contentDescription = null,
                    modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(id = R.string.drying_setting),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row  (modifier = Modifier.padding(horizontal = 32.dp)){
                Text(
                    text = stringResource(id = R.string.drying_duration)+"(s)",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))

                InputView( value = advParamModel.dryingDuration, onValueChange = {
                    advParamModel = advParamModel.copy(dryingDuration = it)
                })
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        //底部菜单
        Row(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .background(color = cardBg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable {
                        advParamModel = AdvParamModel()
                    }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.reset),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            VerticalDivider(
                thickness = 1.dp, color = dividerColor, modifier = Modifier.height(40.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable {
                        if (!advParamModel.isOk()) {
                            ToastUtil.show(context, context.getString(R.string.input_error))
                            return@clickable
                        }

                        SPUtils
                            .getInstance()
                            .put("advParamModel", Gson().toJson(advParamModel))
                        vm.advParamModel = advParamModel

                        scope.launch {
                            vm.setAdvParam(advParamModel)
                            ToastUtil.show(context, context.getString(R.string.save_success))
                        }
                    }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.save),
                    style = MaterialTheme.typography.titleSmall,
                )
            }

        }
    }
}


@Composable
fun InputView(value:String, width: Dp = 90.dp, height: Dp = 36.dp, enabled:Boolean = true, onValueChange:(String)->Unit){
    Box(
        modifier = Modifier
            .size(width, height)
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
            BasicTextField(
                value = value,
                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                singleLine = true,
                enabled = enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Transparent)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                onValueChange = {
                    onValueChange(it)
                })
        }

    }

}