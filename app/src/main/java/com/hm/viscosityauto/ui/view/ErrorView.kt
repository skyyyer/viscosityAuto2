package com.hm.viscosityauto.ui.view

import NoPressStateClick
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hm.viscosityauto.R

// 单例对象管理弹窗状态
object ErrorView {
    var isShow by mutableStateOf(false)
        private set

    private var message by mutableStateOf("加载中...")

    // 👇 新增：关闭回调
    private var onDismissCallback: (() -> Unit)? = null

    fun show(
        msg: String = "加载中...",
        onDismiss: (() -> Unit)? = null
    ) {
        message = msg
        isShow = true
        onDismissCallback = onDismiss
    }

    fun dismiss() {
        isShow = false
        onDismissCallback?.invoke()
        onDismissCallback = null
    }

    @Composable
    fun Content() {
        if (isShow) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.1f))
                    .NoPressStateClick (onClick = { }),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .size(500.dp, 300.dp)
                        .background(Color.White, RoundedCornerShape(5.dp))
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = stringResource(id = R.string.tip),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    BaseButton(title = stringResource(R.string.i_get_it)) {
                        dismiss()
                    }
                }
            }
        }
    }
}