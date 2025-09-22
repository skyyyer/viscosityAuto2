plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.hm.viscosityauto"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hm.viscosityauto"
        minSdk = 24
        targetSdk = 28
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")




    implementation ("com.github.licheedev:Modbus4Android:3.0.0")
    //autosize
    implementation ("com.github.JessYanCoding:AndroidAutoSize:v1.2.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    //导航
    implementation ("io.github.yuexunshi:Nav:1.1.0")
    //Gson
    implementation("com.google.code.gson:gson:2.10.1")
    //权限
    implementation ("com.github.getActivity:XXPermissions:18.63")

    //Room
    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.0")
    kapt ("androidx.room:room-compiler:2.6.0")
    implementation ("androidx.room:room-ktx:2.6.1")

    //Video
    implementation("io.sanghun:compose-video:1.2.0")
    implementation("androidx.media3:media3-exoplayer:1.3.1") // [Required] androidx.media3 ExoPlayer dependency
    implementation("androidx.media3:media3-session:1.3.1") // [Required] MediaSession Extension dependency
    implementation("androidx.media3:media3-ui:1.3.1") // [Required] Base Player UI

    implementation ("org.greenrobot:eventbus:3.2.0")

    implementation ("com.iwdael:wifimanager:1.5.1")


    // 极致体验的Compose刷新组件 (*必须)
    implementation ("com.github.jenly1314.UltraSwipeRefresh:refresh:1.1.0")
// 经典样式的指示器 (可选)
    implementation ("com.github.jenly1314.UltraSwipeRefresh:refresh-indicator-classic:1.1.0")
// Lottie动画指示器 (可选)
    implementation ("com.github.jenly1314.UltraSwipeRefresh:refresh-indicator-lottie:1.1.0")

    //图表
    implementation("io.github.thechance101:chart:Beta-0.0.5")


    //retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")

    //导出excel
    implementation ("org.apache.poi:poi:3.17")

    //bugly
    implementation ("com.tencent.bugly:crashreport:4.1.9.3")

    //app升级
    implementation ("io.github.azhon:appupdate:4.3.6")

}