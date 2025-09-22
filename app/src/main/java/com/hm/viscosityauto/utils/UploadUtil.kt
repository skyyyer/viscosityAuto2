package com.hm.viscosityauto.utils

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

open class UploadUtil {

    @Throws(IOException::class)
    open fun readContentFromPost(getUrl: String?, datajson: String?): String {
        // Post请求的url，与get不同的是不需要带参数
        val postUrl = URL(getUrl)
        // 打开连接
        val connection = postUrl.openConnection() as HttpURLConnection
        // http正文内，因此需要设为true
        connection.doOutput = true
        // Read from the connection. Default is true.
        connection.doInput = true
        // 默认是 GET方式
        connection.requestMethod = "POST"
        // Post 请求不能使用缓存
        connection.useCaches = false
        //设置本次连接是否自动重定向
        connection.instanceFollowRedirects = true
        // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
        // 要注意的是connection.getOutputStream会隐含的进行connect。
        try {
            connection.connect()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        val out = DataOutputStream(connection.outputStream)
        // 正文，正文内容其实跟get的URL中 '? '后的参数字符串一致
        val content = "modelJson=" + URLEncoder.encode(datajson, "UTF-8")
        // DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写到流里面
        out.writeBytes(content)
        //流用完记得关
        out.flush()
        out.close()
        //获取响应
        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        var line: String?
        val stringBuilder = StringBuilder()
        while (reader.readLine().also { line = it } != null) {
            stringBuilder.append(line)
        }
        reader.close()
        //该干的都干完了,记得把连接断了
        connection.disconnect()
        return stringBuilder.toString()
    }


}