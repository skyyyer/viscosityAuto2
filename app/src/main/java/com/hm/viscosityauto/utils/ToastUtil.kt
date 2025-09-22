package com.hm.viscosityauto.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

object ToastUtil {
    // 通用Toast方法
    fun show(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    // 支持资源ID的Toast方法
    fun show(context: Context, @StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, resId, duration).show()
    }

    // 自动获取Activity上下文的扩展（需在Activity中调用）
    fun Activity.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }
}