package com.hm.viscosityauto.utils;

import android.util.Log;


/**
 * Created by Administrator on 2018/6/15.
 */

public class ByteUtil {
    /**
     * 字符串转化成为16进制字符串
     *
     * @param s
     * @return
     */

    public static String strTo16(String s) {
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);

            String s4 = Integer.toHexString(ch);

            str.append(s4);

        }

        return str.toString();

    }

    /**
     * 16进制转换成为string类型字符串
     *
     * @param s
     * @return
     */

    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;

        }

        s = s.replace(" ", "");

        byte[] baKeyword = new byte[s.length() / 2];

        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));

            } catch (Exception e) {
                e.printStackTrace();

            }

        }

        try {
            s = new String(baKeyword, "UTF-8");

            new String();

        } catch (Exception e1) {
            e1.printStackTrace();

        }

        return s;

    }

    /**
     * 将int类型的数值转换为十六进制字符串，并自动补足至至少两位。
     * @param number 待转换的整数
     * @return 补足至两位的十六进制字符串
     */
    public static String intToHex(int number) {
        // 使用String.format方法转换并补足至两位，X表示大写十六进制，x表示小写十六进制
        // %02X表示输出至少两位，不够则前面补0，大写形式；若需要小写形式，可使用%02x
        return String.format("%02X", number);
    }

    public static String intToHex4(int number) {
        // 使用String.format方法转换并补足至两位，X表示大写十六进制，x表示小写十六进制
        // %02X表示输出至少两位，不够则前面补0，大写形式；若需要小写形式，可使用%02x
        return String.format("%04X", number);
    }

    /**
     * 向串口发送数据转为字节数组
     */

    public static byte[] hex2byte(String hex) {
        String digital = "0123456789 ABCDEF";

        String hex1 = hex.replace(" ", "");

        char[] hex2char = hex1.toCharArray();

        byte[] bytes = new byte[hex1.length() / 2];

        byte temp;

        for (int p = 0; p < bytes.length; p++) {
            temp = (byte) (digital.indexOf(hex2char[2 * p]) * 16);

            temp += digital.indexOf(hex2char[2 * p + 1]);

            bytes[p] = (byte) (temp & 0xff);

        }

        return bytes;

    }


    public static byte[] hexStringToByteArray(String s) {
        // 检查字符串长度是否为奇数
        if (s.length() % 2 != 0) {
            // 在末尾补足一个 '0'
            s = "0"+s;
        }

        int len = s.length();

        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }


    /**
     * 接收到的字节数组转换16进制字符串
     */

    public static String bytes2HexString(byte[] b, int size) {
        String ret = "";

        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);

            if (hex.length() == 1) {
                hex = "0"+hex;

            }

            ret += hex.toUpperCase();

        }

        return ret;

    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");

        if (src == null || src.length <= 0) {
            return null;

        }

        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;

            String hv = Integer.toHexString(v);

            if (hv.length() < 2) {
                stringBuilder.append(0);

            }

            stringBuilder.append(hv);

        }

        return stringBuilder.toString();

    }

    /**
     * 接收到的字节数组转换16进制字符串
     */

    public static String byteToStr(byte[] b, int size) {
        String ret = "";

        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);

            if (hex.length() == 1) {
                hex = "0"+hex;

            }

            ret += hex.toUpperCase();

        }

        return ret;

    }


    /**
     * 计算CRC16校验码
     * <p>
     * 逐个求和
     *
     * @param bytes 字节数组
     * @return {@link String} 校验码
     * @since 1.0
     */

    public static String getCRC_16(byte[] bytes) {
        int CRC = 0x0000ffff;

        int POLYNOMIAL = 0x0000a001;

        int i, j;

        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);

            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;

                    CRC ^= POLYNOMIAL;

                } else {
                    CRC >>= 1;

                }

            }

        }

        if (Integer.toHexString(CRC).toUpperCase().length() == 2) {
            return byteToStr(bytes, bytes.length) + "00"+Integer.toHexString(CRC).toUpperCase();

        } else if (Integer.toHexString(CRC).toUpperCase().length() == 3) {
            return byteToStr(bytes, bytes.length) + "0"+Integer.toHexString(CRC).toUpperCase();

        }

        return byteToStr(bytes, bytes.length) + Integer.toHexString(CRC).toUpperCase();

    }


    /**
     * 计算字节数组的异或校验码
     *
     * @param data 字节数组
     * @return 异或校验码
     */
    public static int calculateXorChecksum(byte[] data) {
        int checksum = 0;
        for (byte b : data) {
            checksum ^= b;
        }
        Log.d("calculateXorChecksum",String.valueOf(checksum));
        // 将校验码转换为无符号表示
        checksum = checksum & 0xFF;
        return checksum;
    }

    /**
     * 指令校验和,并取出后两位字节
     */

    public static String getSum16(byte[] msg, int length) {
        long mSum = 0;

        byte[] mByte = new byte[length];

/** 逐Byte添加位数和 */

        for (byte byteMsg : msg) {
            long mNum = ((long) byteMsg >= 0) ? (long) byteMsg : ((long) byteMsg + 256);

            mSum += mNum;

        } /** end of for (byte byteMsg : msg) */

/** 位数和转化为Byte数组 */

        for (int liv_Count = 0; liv_Count < length; liv_Count++) {
            mByte[length - liv_Count - 1] = (byte) (mSum >> (liv_Count * 8) & 0xff);

        } /** end of for (int liv_Count = 0; liv_Count < length; liv_Count++) */

        return byteToStr(msg, length) + byteToStr(mByte, mByte.length).substring(byteToStr(mByte, mByte.length).length() - 4, byteToStr(mByte, mByte.length).length());

    }



    /**
     * 字节数组转16进制
     * @param bytes 需要转换的byte数组
     * @return  转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }


    /**
     * 打印Log数据
     * @param data
     */
    public static void printByteArray(byte[] data) {

        StringBuilder hexString = new StringBuilder();
        for (byte b : data) {
            String hex = Integer.toHexString(0xFF & b);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex).append(" ");
        }
        Log.d("printByteArray",
                 hexString.toString());
    }

}