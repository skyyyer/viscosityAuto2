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
# 保留 Room 相关类
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keepclassmembers class * {
    @androidx.room.* *;
}
-keep class * extends androidx.room.RoomDatabase$Callback

# 保留 Entity 类
-keep class com.hm.viscosityauto.database.entity.** { *; }

# 保留 DAO 接口
-keep interface com.hm.viscosityauto.database.dao.** { *; }


-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# 如果使用索引
-keep class org.greenrobot.eventbus.meta.** { *; }
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}

# 保留订阅者类
-keep class com.example.myapp.** { *; }
-keepnames class * extends java.lang.Object
-keepclassmembers class * {
    public void onEvent*(**);
}
