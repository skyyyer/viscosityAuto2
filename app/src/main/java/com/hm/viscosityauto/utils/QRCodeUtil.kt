package com.hm.viscosityped.utils

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set


object QRCodeUtil {


    public const val DEFAULT_PRODUCT_MODEL = "HM-YN2"
     const val SUPPORT_URL_PREFIX =
        "http://cpk.hengmeierp.com/cpk/productList/detailCRM/"
    private const val QR_SIZE = 520


    /**
     * 生成二维码 Bitmap
     * @param model  设备型号
     * @param size 生成的图片尺寸（像素）
     * @param margin 边距（单位：像素）
     */
    fun generate(
        model: String = DEFAULT_PRODUCT_MODEL,
        language:String = "EN",
        size: Int = 512,
        margin: Int = 4
    ): Bitmap? {
        return try {
            val hints = mapOf(
                EncodeHintType.CHARACTER_SET to "UTF-8",
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H, // 纠错等级 H（最高）
                EncodeHintType.MARGIN to margin
            )

            val writer = QRCodeWriter()
            val bitMatrix = writer.encode("$SUPPORT_URL_PREFIX$model/$language", BarcodeFormat.QR_CODE, size, size, hints)

            val bitmap = createBitmap(size, size, Bitmap.Config.RGB_565)

            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap[x, y] = if (bitMatrix[x, y]) android.graphics.Color.BLACK
                    else android.graphics.Color.WHITE
                }
            }

            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 生成彩色二维码
     */
    fun generateColored(
        model: String = DEFAULT_PRODUCT_MODEL,
        size: Int = 512,
        foregroundColor: Int = android.graphics.Color.BLACK,
        backgroundColor: Int = android.graphics.Color.WHITE
    ): Bitmap? {
        return try {
            val hints = mapOf(
                EncodeHintType.CHARACTER_SET to "UTF-8",
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
                EncodeHintType.MARGIN to 4
            )
            
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(SUPPORT_URL_PREFIX+model, BarcodeFormat.QR_CODE, size, size, hints)
            
            val bitmap = createBitmap(size, size, Bitmap.Config.RGB_565)
            
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap[x, y] = if (bitMatrix[x, y]) foregroundColor
                    else backgroundColor
                }
            }
            
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}