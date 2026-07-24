package com.hm.viscosityauto.ui.page

import android.app.Activity
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asi.nav.Nav
import com.hm.viscosityauto.R
import com.hm.viscosityauto.ui.theme.cardBgBlue
import com.hm.viscosityauto.ui.view.BaseButton
import com.hm.viscosityauto.ui.view.BaseTitle
import com.hm.viscosityauto.utils.ToastUtil
import com.hm.viscosityauto.utils.ota.OtaStatus
import com.hm.viscosityauto.vm.MainVM
import com.hm.viscosityauto.vm.SettingVM
import me.rosuh.filepicker.bean.FileItemBeanImpl
import me.rosuh.filepicker.config.AbstractFileFilter
import me.rosuh.filepicker.config.FilePickerConfig
import me.rosuh.filepicker.config.FilePickerManager
import me.rosuh.filepicker.filetype.FileType
import me.rosuh.filepicker.filetype.RasterImageFileType

@Composable
fun FirmUpdatePage(mainVM: MainVM, vm: SettingVM = viewModel()) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.getPumpMotorVer()
    }


    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(vertical = 28.dp, horizontal = 32.dp)
        ) {
            //标题
            BaseTitle(title = stringResource(id = R.string.firmware_update), onBack = {
                if (vm.otaController != null && vm.otaController?.otaStatus!! in 1..99) {
                    ToastUtil.show(context, context.getString(R.string.updating))
                    return@BaseTitle
                }

                Nav.back()
            })


            Spacer(modifier = Modifier.height(40.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                BaseButton(title = stringResource(id = R.string.sel_file)) {
                    if (vm.otaController != null && vm.otaController?.otaStatus!! in 1..99) {
                        ToastUtil.show(context, context.getString(R.string.updating))
                        return@BaseButton
                    }


                    vm.otaController = null

                    FilePickerManager
                        .from(context as Activity)
                        .maxSelectable(1)
                        .filter(object : AbstractFileFilter() {
                            override fun doFilter(listData: ArrayList<FileItemBeanImpl>): ArrayList<FileItemBeanImpl> {
                                return ArrayList(listData.filter { item ->

                                    item.isDir ||
                                            item.fileName.lowercase().endsWith(".bin")

                                })
                            }
                        })
                        .forResult(FilePickerManager.REQUEST_CODE)
                }
                Spacer(modifier = Modifier.width(24.dp))

                Text(text = mainVM.firmPath.value)
            }
            Spacer(modifier = Modifier.height(48.dp))

            BaseButton(
                title = stringResource(id = R.string.update),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 200.dp)
            ) {
                if (mainVM.firmPath.value.isEmpty()) {
                    ToastUtil.show(context, context.getString(R.string.pls_sel_file))
                    return@BaseButton
                }
                if (vm.otaController != null && vm.otaController?.otaStatus!! in 1..99) {
                    ToastUtil.show(context, context.getString(R.string.updating))
                    return@BaseButton
                }

                vm.setFirmControl(mainVM.firmPath.value)

                vm.otaController?.start()
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.dont_operate_in_upgrade),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (vm.otaController == null) {
                    Text(stringResource(R.string.ota_state_idle))

                } else {
                    Text(stringResource(getStatusStringRes(vm.otaController!!.otaStatus)))
                }

                if (vm.otaController == null) {
                    Text("0%")
                } else {
                    Text("${vm.otaController!!.progress}%")
                }
            }

            if (vm.otaController == null) {
                LinearProgressIndicator(
                    progress = { 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    strokeCap = StrokeCap.Round,
                    color = cardBgBlue
                )
            } else {
                vm.otaController?.let {
                    LinearProgressIndicator(
                        progress = { vm.otaController!!.progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        strokeCap = StrokeCap.Round,
                        color = cardBgBlue
                    )
                }
            }


        }
    }
}

private fun getStatusStringRes(status: Int): Int = when (status) {
    OtaStatus.IDLE -> R.string.ota_state_idle
    OtaStatus.ENTER_OTA -> R.string.ota_state_enter_ota
    OtaStatus.WAIT_BOOT -> R.string.ota_state_wait_boot
    OtaStatus.SEND_HEADER -> R.string.ota_state_send_header
    OtaStatus.SEND_DATA -> R.string.ota_state_send_data
    OtaStatus.SEND_EOT -> R.string.ota_state_send_eot
    OtaStatus.SEND_EMPTY -> R.string.ota_state_send_empty
    OtaStatus.WAIT_REBOOT -> R.string.ota_state_wait_reboot
    OtaStatus.QUERY_VERSION -> R.string.ota_state_query_version
    OtaStatus.SUCCESS -> R.string.ota_state_success
    OtaStatus.FAIL -> R.string.ota_state_fail
    OtaStatus.FAIL_RETRY -> R.string.ota_state_fail_retry
    OtaStatus.FAIL_CANCEL -> R.string.ota_state_fail_cancel
    else -> R.string.ota_state_fail
}


