package com.hm.viscosityauto.utils

import android.os.Handler
import android.os.Looper

class CountTimer(private val intervalMillis: Long = 1000L) {

    private var startTimeMillis: Long = 0
    private var currentTimeMillis: Long = 0
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private var isRunning = false

    // 设置时间更新监听器
    var onTimeUpdate: ((elapsedTime: Long) -> Unit)? = null

    /**
     * 开始计时
     */
    fun start() {
        if (!isRunning) {
            isRunning = true
            startTimeMillis = System.currentTimeMillis()
            runnable = Runnable {
                currentTimeMillis = System.currentTimeMillis() - startTimeMillis
                onTimeUpdate?.invoke(currentTimeMillis)
                handler.postDelayed(runnable!!, intervalMillis)
            }
            handler.post(runnable!!)
        }
    }

    /**
     * 停止计时
     */
    fun stop() {
        isRunning = false
        handler.removeCallbacksAndMessages(null)
    }

    fun isTimerRun():Boolean{
        return  isRunning
    }
}
