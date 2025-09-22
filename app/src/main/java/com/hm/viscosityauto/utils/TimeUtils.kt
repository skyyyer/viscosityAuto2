package com.hm.viscosityauto.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.SystemClock
import com.hm.viscosityauto.R
import java.io.DataOutputStream
import java.text.SimpleDateFormat
import java.util.Date


object TimeUtils {
    /**
     * 将时间戳（毫秒）转换为年月日时分秒格式的字符串
     *
     * @param timestamp 毫秒级时间戳
     * @return 年月日时分秒格式的字符串，如 "2024-0½-23 11:20:41"
     */
    @SuppressLint("SimpleDateFormat")
    fun timestampToString(timestamp: Long = System.currentTimeMillis()): String {
        // 定义日期/时间格式字符串，
        val pattern = "yyyy-MM-dd HH:mm:ss"
        // 创建SimpleDateFormat对象并传入指定的格式字符串
        val sdf = SimpleDateFormat(pattern)
        // 使用时间戳构造Date对象
        val date = Date(timestamp)



        // 使用SimpleDateFormat将Date对象格式化为字符串
        return sdf.format(date)
    }

    fun splitDateTime(dateTime: String): Pair<String, String> {
        val parts = dateTime.split(" ")
        return when {
            parts.size >= 2 -> parts[0] to parts[1]
            parts.size == 1 -> parts[0] to "00:00:00" // 只有日期部分
            else -> "" to ""
        }
    }

    // 设置新的系统时间，单位毫秒
    fun setSystemTime(newTime: Long) {
        try {
            // 使用Runtime执行命令需要root权限
            Runtime.getRuntime().exec(arrayOf("date", newTime.toString()))
            // 或者使用SystemClock修改时间
            SystemClock.setCurrentTimeMillis(newTime)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 设置系统时间
     *
     * @param year   年份
     * @param month  月份（0-11）
     * @param day    日期
     * @param hour   小时
     * @param minute 分钟
     * @param second 秒钟
     */
    fun setSystemTime(
        year: Int,
        month: Int,
        dayOfMonth: Int,
        hour: Int,
        minute: Int,
        sec: Int,
        isNet: Boolean
    ) {
        val timeSb = StringBuilder()

        timeSb.delete(0, timeSb.length)
        timeSb.append(if (month < 10) "0$month" else month)
        timeSb.append(if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth)
        timeSb.append(if (hour < 10) "0$hour" else hour)
        timeSb.append(if (minute < 10) "0$minute" else minute)
        timeSb.append(year)
        timeSb.append(".")
        if (isNet) {
            timeSb.append(if (sec < 10) "0$sec" else sec)
        } else {
            timeSb.append("00")
        }
        try {
            val process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process.outputStream)
            os.writeBytes("date $timeSb set \n")
            os.writeBytes("busybox hwclock -w\n")
            os.writeBytes("exit\n")
            os.flush()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun timestampToCusString(timestamp: Long = System.currentTimeMillis(),format:String = "yyyy-MM-dd-HH-mm-ss"): String {
        // 创建SimpleDateFormat对象并传入指定的格式字符串
        val sdf = SimpleDateFormat(format)
        // 使用时间戳构造Date对象
        val date = Date(timestamp)
        // 使用SimpleDateFormat将Date对象格式化为字符串
        return sdf.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    fun timestampToTime(timestamp: Long = System.currentTimeMillis()): String {
        // 定义日期/时间格式字符串，
        val pattern = "HH:mm:ss"
        // 创建SimpleDateFormat对象并传入指定的格式字符串
        val sdf = SimpleDateFormat(pattern)
        // 使用时间戳构造Date对象
        val date = Date(timestamp)



        // 使用SimpleDateFormat将Date对象格式化为字符串
        return sdf.format(date)
    }


    @SuppressLint("SimpleDateFormat")
    fun timestampToDate(timestamp: Long = System.currentTimeMillis()): String {
        // 定义日期/时间格式字符串，
        val pattern = "yyyy-MM-dd"
        // 创建SimpleDateFormat对象并传入指定的格式字符串
        val sdf = SimpleDateFormat(pattern)
        // 使用时间戳构造Date对象
        val date = Date(timestamp)
        // 使用SimpleDateFormat将Date对象格式化为字符串
        return sdf.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    fun timestampToDayOfWeek(context: Context, timestamp: Long= System.currentTimeMillis()): String {
        // 定义日期/时间格式字符串，
        val pattern = "yyyy-MM-dd"
        // 创建SimpleDateFormat对象并传入指定的格式字符串
        val sdf = SimpleDateFormat(pattern)
        // 使用时间戳构造Date对象
        val date = Date(timestamp)
        // 使用SimpleDateFormat将Date对象格式化为字符串
        val weekDays = arrayOf(
            context.getString(R.string.Sunday),
            context.getString(R.string.Monday),
            context.getString(R.string.Tuesday),
            context.getString(R.string.Wednesday),
            context.getString(R.string.Thursday),
            context.getString(R.string.Friday),
            context.getString(R.string.Saturday)
        )

        return weekDays[date.day]
    }



    /**
     * 秒数转换成 时分秒
     */
    fun secondsToHMS(totalSeconds: Int): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    /**
     * 秒数转换成 分秒
     */
    fun secondsToMS(totalSeconds: Int): String {
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

}

