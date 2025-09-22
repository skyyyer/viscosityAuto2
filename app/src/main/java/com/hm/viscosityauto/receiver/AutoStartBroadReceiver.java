package com.hm.viscosityauto.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hm.viscosityauto.MainActivity;


/**
 * @author Martin-harry
 * @date 2022/3/4
 * @address
 * @Desc 定义自启动BroadcastReceiver
 */
public class AutoStartBroadReceiver extends BroadcastReceiver {
    private static final String ACTION = Intent.ACTION_BOOT_COMPLETED;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("接收广播", "onReceive: " + intent.getAction());
        //开机启动
        if (ACTION.equals(intent.getAction())) {
            Log.e("接收广播", "onReceive: 启动了。。。");

//第一种方式：根据包名
//            PackageManager packageManager = context.getPackageManager();
//            Intent mainIntent = packageManager.getLaunchIntentForPackage("com.hm.viscosityauto");
//            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(mainIntent);
//            context.startService(mainIntent);


//第二种方式：指定class类，跳转到相应的Acitivity
            Intent mainIntent = new Intent(context, MainActivity.class);
            /**
             * Intent.FLAG_ACTIVITY_NEW_TASK
             * Intent.FLAG_ACTIVITY_CLEAR_TOP
             */
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainIntent);
        }
    }

}
