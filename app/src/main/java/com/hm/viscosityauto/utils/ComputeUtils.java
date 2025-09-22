package com.hm.viscosityauto.utils;

import android.widget.Toast;

import com.hm.viscosityauto.MyApp;
import com.hm.viscosityauto.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class ComputeUtils {

    /**
     * @return double
     * @description 求两个double数据类型相除，并解决科学计数法问题
     * @date 2022/5/21 12:44
     * @param[ v1, v2, scale]
     */
    public static String division(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        double resultValue = b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        //防止出现科学计数法
        BigDecimal bigDecimal1 = new BigDecimal(resultValue);
        String finishResult = bigDecimal1.setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
        return finishResult;
    }


    /**
     * float 相除 保留4位小数
     *
     * @param a
     * @param b
     * @return
     */
    public static Float divideAndFormat(float a, float b) {
        if (b == 0) {
            Toast.makeText(MyApp.getInstance(), MyApp.getInstance().getString(R.string.data_error), Toast.LENGTH_SHORT).show();
            return 0f;
        }
        float result = a / b;
        DecimalFormat df = new DecimalFormat("#.0000"); // 创建一个DecimalFormat实例来格式化数字
        return Float.valueOf(df.format(result)); // 使用DecimalFormat格式化结果
    }

    /**
     * float  int 相除 保留2位小数
     *
     * @param a
     * @param b
     * @return
     */
    public static Float divideAndFormat(float a, int b) {
        if (b == 0) {
            Toast.makeText(MyApp.getInstance(), MyApp.getInstance().getString(R.string.data_error), Toast.LENGTH_SHORT).show();
            return 0f;
        }
        float result = a / b;
        DecimalFormat df = new DecimalFormat("#.00"); // 创建一个DecimalFormat实例来格式化数字
        return Float.valueOf(df.format(result)); // 使用DecimalFormat格式化结果
    }


    /**
     * float  保留2位小数
     *
     * @param a
     * @return
     */
    public static Float floatFormat(float a) {
        DecimalFormat df = new DecimalFormat("0.00"); // 创建一个DecimalFormat实例来格式化数字
        return Float.valueOf(df.format(a)); // 使用DecimalFormat格式化结果
    }

    /**
     * float 相乘 保留4位小数
     *
     * @param a
     * @param b
     * @return
     */
    public static Float multiplyAndFormat(float a, float b) {
        float result = a * b;
        DecimalFormat df = new DecimalFormat("#.0000"); // 创建一个DecimalFormat实例来格式化数字
        return Float.valueOf(df.format(result)); // 使用DecimalFormat格式化结果
    }


    /**
     * 电机速度转化 60 -》100
     */
    public static int moterSpeedConvert(int speed) {
        return speed * 100 / 60; // 使用DecimalFormat格式化结果
    }


}
