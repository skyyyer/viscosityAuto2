package com.hm.viscosityauto.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

// 单例对象管理弹窗状态
object LoadingDialog {
    private var isShow by mutableStateOf(false)
    private var message by mutableStateOf("加载中...")

    // 显示弹窗
    fun show(msg: String = "加载中...") {
        message = msg
        isShow = true
    }

    // 隐藏弹窗
    fun dismiss() {
        isShow = false
    }

    fun getShowState(): Boolean{
        return isShow
    }
    // Compose 内容组件
    @Composable
    fun Content() {
        if (isShow) {
            Dialog(onDismissRequest = {}) {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}