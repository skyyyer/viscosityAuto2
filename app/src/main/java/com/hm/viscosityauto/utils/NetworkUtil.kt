package com.hm.viscosityauto.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.hm.viscosityauto.R

object NetworkUtil {

    /**
     * 检查设备是否有可用的网络连接。
     *
     * @param context 上下文
     * @return 如果有可用的网络连接返回 true；否则返回 false。
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        // Android 10 (API level 29) 及以上版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager?.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> true
                else -> false
            }
        } else {
            // Android 9.0 (API level 28) 及以下版本
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager?.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }


    /**
     * 根据 信号强弱 返回对应图片
     *
     * @return 图片id
     */
    fun getWifiImage(level: Int): Int {
        return if (level >= -50) {
            R.mipmap.signal_three
        } else if (level >= -70) {
            R.mipmap.signal_two

        } else if (level >= -80) {
            R.mipmap.signal_one
        } else {
            R.mipmap.signal_zero
        }
    }
}
