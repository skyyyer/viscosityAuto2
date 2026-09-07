# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# ============================ 基本规则 ============================
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keepattributes SourceFile, LineNumberTable
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep class * extends androidx.lifecycle.ViewModel

# ============================ 序列化/反序列化 ============================
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ============================ 枚举 ============================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ============================ AndroidX ============================
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

# ============================ Jetpack Compose ============================
-keep class androidx.compose.** { *; }
-keep class kotlinx.coroutines.** { *; }

# ============================ Gson ============================
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*


# ============================ Media3/ExoPlayer ============================
-keep class androidx.media3.** { *; }
-keep class com.google.android.exoplayer2.** { *; }

# ============================ EventBus ============================
-keep class org.greenrobot.eventbus.** { *; }
-keepattributes *Annotation*
-keepclassmembers class * {
    public void onEvent*(**);
    @org.greenrobot.eventbus.Subscribe <methods>;
}

# ============================ Retrofit + OkHttp ============================
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# ============================ Apache POI ============================
-keep class org.apache.poi.** { *; }
-keep class org.apache.xmlbeans.** { *; }
-keep class org.apache.commons.** { *; }
-dontwarn org.apache.**
-dontwarn org.openxmlformats.**
-dontwarn schemasMicrosoftCom.**
-dontwarn javax.xml.**
-dontwarn org.w3c.**

# ============================ Bugly ============================
-keep class com.tencent.bugly.** { *; }

# ============================ MPAndroidChart ============================
-keep class com.github.mikephil.charting.** { *; }

# ============================ 第三方库 ============================
-keep class io.github.yuexunshi.** { *; }          # Navigation
-keep class com.hjq.permissions.** { *; }         # XXPermissions
-keep class io.github.azhon.** { *; }             # AppUpdate
-keep class com.github.jenly1314.** { *; }        # UltraSwipeRefresh
-keep class com.github.licheedev.** { *; }        # Modbus4Android
-keep class com.iwdael.** { *; }                  # WiFiManager
-keep class dev.shreyaspatil.** { *; }            # Capturable

# ============================ 视频相关 ============================
-keep class io.sanghun.** { *; }                  # Compose Video


# 保持 SerialPort 类的所有字段和方法不被混淆
-keep class android.serialport.SerialPort {
    *;
}

# 或者更精确地保护特定字段
-keepclassmembers class android.serialport.SerialPort {
    private java.io.FileDescriptor mFd;
    public <methods>;
    private <methods>;
}

# 通用忽略警告
-dontwarn **