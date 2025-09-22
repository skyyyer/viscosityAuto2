package com.hm.viscosityauto.utils;

import android.serialport.SerialPort;
import android.util.Log;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPortManager {
    private static final String TAG = "SerialPortManager";
    private SerialPort serialPort;
    private InputStream inputStream;
    private OutputStream outputStream;
    private OnDataReceivedListener mListener;

    public static final String CRC = "00";

    public static final String HEAD = "faaf";
    public static final String FOOT = "eaae";

    //读取缸内温度
    public static final String CMD_READ_T = "01";

    //介质控制
    public static final String MEDIUM_VALUE = "04";
    //照明控制
    public static final String CMD_LIGHT = "06";
    //加热控制
    public static final String CMD_SET_T = "07";

    //a通道设置
    public static final String A_CMD = "08";
    //b通道设置
    public static final String B_CMD = "09";

    //清洗时间
    public static final String CMD_SET_CLEAN_DURATION = "10";


    //进清洗液时间
    public static final String CMD_LIQUID_ENTER_DURATION = "11";

    //抽提时间
    public static final String CMD_EXTRACT_DURATION = "12";
    //抽提间隔
    public static final String CMD_EXTRACT_INTERVAL = "13";

    //A检测值
    public static final String A_VALUE = "14";
    //B检测值
    public static final String B_VALUE = "15";


    //AB 检测值上报
    public static final String AB_VALUE_UP = "17";

    //A上设定值 灵敏度
    public static final String A_UP_SET = "18";
    //A下设定值 灵敏度
    public static final String A_DOWN_SET = "19";
    //B上设定值 灵敏度
    public static final String B_UP_SET = "20";
    //B下设定值 灵敏度
    public static final String B_DOWN_SET = "21";


    //电磁阀测试
    public static final String SV_TEST = "26";
    //电机测试
    public static final String MOTOR_SEN = "27";

    //测试模式
    public static final String DEBUG_MODE = "28";
    //A状态
    public static final String A_STATE = "29";
    //B状态
    public static final String B_STATE = "30";
    //加热状态
    public static final String HEATING_STATE = "31";

    //电机速度
    public static final String MOTOR_SPEED = "32";


    //排空电机速度
    public static final String EMPTY_MOTOR_SPEED = "33";
    //排空抽提时间
    public static final String EMPTY_EXTRACT_DURATION = "34";
    //排空抽提间隔
    public static final String EMPTY_EXTRACT_INTERVAL = "35";
    // 排空烘干时间
    public static final String EMPTY_DRYING_DURATION = "36";
    //清洗电机速度
    public static final String CLEAN_MOTOR_SPEED = "37";
    //清洗烘干时间
    public static final String CLEAN_DRYING_DURATION = "38";

    //泄压时间
    public static final String DECOM_P_DURATION = "39";


    public interface OnDataReceivedListener {

        void onTemperatureReceived(String temperature);

        void onLightStateReceived(Boolean state);

        void onHeatingState(int state);

        void onADeviceState(int state);

        void onBDeviceState(int state);

        void onADetectedValue(int valueUp,int valueDown);

        void onBDetectedValue(int valueUp,int valueDown);
    }

    public SerialPortManager(String devicePath, int baudRate, OnDataReceivedListener listener) {
        mListener = listener;
        try {
            File device = new File(devicePath);
            serialPort = new SerialPort(device, baudRate);
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();

            // 开启监听线程读取串口数据
            new Thread(() -> {
                byte[] buffer = new byte[10];
                int len;
                while (true) {
                    try {
                        len = inputStream.read(buffer);
                        if (len>=6){
                            Log.e(TAG, "reading  " + ByteUtil.bytesToHex(buffer)+"   "+len);
                        }
                        if (len == 10) {
                            String readString = ByteUtil.bytesToHex(buffer);
                            String head = readString.substring(0, 4);
                            String foot = readString.substring(16, 20);
                            String data = readString.substring(6, 14);
                            String cmd = readString.substring(4, 6);
                            Log.i(TAG, "reading from InputStream:   " + ByteUtil.bytesToHex(buffer) + "   CMD: " + cmd + "   data: " + data);

                            if (head.equals(HEAD) && foot.equals(FOOT)) {

                                switch (cmd) {
                                    case CMD_READ_T:
                                        String decimal = "00";
                                        try {
                                            if (Integer.parseInt(data.substring(2, 4), 16) >= 10) {
                                                decimal = String.valueOf(Integer.parseInt(data.substring(2, 4), 16));
                                            } else {
                                                decimal = "0" + Integer.parseInt(data.substring(2, 4), 16);
                                            }
                                            String t = Integer.parseInt(data.substring(0, 2), 16) + "." + decimal;
                                            mListener.onTemperatureReceived(t);
                                        } catch (NumberFormatException ignored) {

                                        }

                                        break;
                                    case CMD_LIGHT:
                                        mListener.onLightStateReceived(data.charAt(1) == '1');
                                        break;

                                    case A_STATE:
                                        int AState = Integer.parseInt(String.valueOf(data.charAt(1)));
                                        mListener.onADeviceState(AState);

                                        Log.d(TAG, "A_STATE:   " +AState);

                                        break;
                                    case B_STATE:
                                        int BState = Integer.parseInt(String.valueOf(data.charAt(1)));
                                        mListener.onBDeviceState(BState);
                                        Log.d(TAG, "B_STATE:   " +BState);
                                        break;

                                    case HEATING_STATE:
                                        int heatState = Integer.parseInt(String.valueOf(data.charAt(1)));

                                        mListener.onHeatingState(heatState);
                                        Log.d(TAG, "HEATING_STATE:   " +heatState);
                                        break;

                                    case A_VALUE:
                                        int aUp = Integer.parseInt(data.substring(0, 4),16);
                                        int aDown = Integer.parseInt(data.substring(4, 8),16);
                                        mListener.onADetectedValue(aUp,aDown);
                                        break;

                                    case B_VALUE:
                                        int bUp = Integer.parseInt(data.substring(0, 4),16);
                                        int bDown = Integer.parseInt(data.substring(4, 8),16);
                                        mListener.onBDetectedValue(bUp,bDown);
                                        break;
                                }
                            }

                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading from InputStream", e);
                        break;
                    }
                }
            }).start();
        } catch (IOException e) {
            Log.e(TAG, "Error opening serial port", e);
        }
    }

    public void write(byte[] data) {
        try {
            outputStream.write(data);
        } catch (IOException e) {
            Log.e(TAG, "Error writing to OutputStream", e);
        }
    }

    public void close() {
        try {
            if (outputStream != null) outputStream.close();
            if (inputStream != null) inputStream.close();
            if (serialPort != null) serialPort.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing serial port", e);
        }
    }

    public static void listSerialPorts() {
        try {
            // 获取系统中所有串口设备的文件目录
            File devDir = new File("/dev");
            // 列出所有文件
            File[] files = devDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getAbsolutePath().startsWith("/dev/ttyS") ||
                            file.getAbsolutePath().startsWith("/dev/ttyUSB") ||
                            file.getAbsolutePath().startsWith("/dev/ttyACM")) {
                        // 打印找到的串口地址
                        System.out.println("Serial Port: " + file.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
