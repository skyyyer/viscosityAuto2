package com.hm.viscosityauto.utils

import android.content.Context
import com.hm.viscosityauto.R
import com.hm.viscosityauto.vm.MaxT

/**
 * 输入  检测
 */
object LimitUtil {
    // 检测输入 是否超出限制
    fun isOverLimit(context: Context, temperature: Float = 0f): Boolean {
        if (temperature > MaxT || temperature < 0) {
            ToastUtil.show(
                context,
                context.getString(R.string.over_limit)
            )
            return true
        }


        return false

    }

    // 检测输入 是否超出限制  和 错误
    fun isOverLimit(context: Context, temperature: String = "0"): Boolean {
        if (temperature.toFloatOrNull() == null
        ) {
            ToastUtil.show(context, context.getString(R.string.input_error))
            return true
        }

        if (temperature.toFloat() > MaxT || temperature.toFloat() < 0) {
            ToastUtil.show(
                context,
                context.getString(R.string.over_limit)
            )
            return true
        }
        return false
    }
}