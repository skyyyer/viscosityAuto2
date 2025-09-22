//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hm.viscosityauto.utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

import kotlin.text.Charsets;

public class StringUtils {
    public StringUtils() {
    }

    public static byte[] str2Bytes(String str) {
        byte[] data = null;

        try {
            byte[] b = str.getBytes("utf-8");
            data = (new String(b, "utf-8")).getBytes("gbk");
        } catch (UnsupportedEncodingException var3) {
            var3.printStackTrace();
        }

        return data;
    }

    public static String bytes2HexString(byte[] array) {
        StringBuilder builder = new StringBuilder();
        if (array != null) {
            byte[] var2 = array;
            int var3 = array.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                byte b = var2[var4];
                String hex = Integer.toHexString(b & 255);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }

                builder.append(hex + " ");
            }
        }

        return builder.toString().toUpperCase();
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    public static byte[] str2Bytes(String str, String charset) {
        byte[] data = null;

        try {
            byte[] b = str.getBytes("utf-8");
            data = (new String(b, "utf-8")).getBytes(charset);
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
        }

        return data;
    }

    public static byte[] strToBytesByLanguage(String str) {
        byte[] data = null;
        String charset = "GB2312";
        String language = SPUtils.getInstance().getString("language", "zh");
        if (language.equals("zh")) {
            charset = "GB2312";
        } else if (language.equals("ja")) {
            charset = "Shift_JIS";
        } else {
            charset = "GB2312";
        }

        try {
            byte[] b = str.getBytes(Charsets.UTF_8);
            data = (new String(b, Charsets.UTF_8)).getBytes(charset);
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
        }

        return data;
    }


    /**
     * 将给定的字符串转换为浮点数，并格式化为保留两位小数的字符串，
     * 不进行四舍五入，确保整数位为0时也能显示。
     *
     * @param strNum 要转换和格式化的字符串
     * @return 格式化后的字符串
     */
    public static String string2Float(String strNum) {
        try {
            float num = Float.parseFloat(strNum);
            DecimalFormat df = new DecimalFormat("0.00");
//            df.setRoundingMode(RoundingMode.UP);
            Log.e("input",df.format(num));
            return df.format(num);
        } catch (NumberFormatException e) {
            // 处理无效输入的情况
            return "Invalid input";
        }
    }
}
