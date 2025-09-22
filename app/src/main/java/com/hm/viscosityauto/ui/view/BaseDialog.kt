package com.hm.viscosityauto.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import me.jessyan.autosize.AutoSizeCompat
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.utils.AutoSizeUtils

@Composable
fun BaseDialog(
    dialogState: MutableState<Boolean>,
    onDismissRequest: () -> Unit = { dialogState.value = false },
    contentView: @Composable () -> Unit? = {},
) {

    if (dialogState.value) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            contentView()
        }
    }
}
