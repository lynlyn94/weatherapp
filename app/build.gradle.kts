plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // 【新增】应用 Compose 编译器插件
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.weatherapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.weatherapp"
        minSdk = 26
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    // 【重要操作】请删除或注释掉下面这个 composeOptions 代码块
    // Kotlin 2.0 不再需要这几行了，留着会报错！
    /* composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    */

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // --- 核心 AndroidX 库 ---
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.3")

    // --- Jetpack Compose (UI) ---
    // 使用 BOM (Bill of Materials) 统一管理 Compose 版本
    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    // Material You (Material 3) - 核心 UI 库
    implementation("androidx.compose.material3:material3")
    // 扩展图标库 (可选，包含更多天气相关图标)
    implementation("androidx.compose.material:material-icons-extended")

    // --- 导航 (Navigation) ---
    implementation("androidx.navigation:navigation-compose:2.8.3")

    // --- 网络请求 (Networking) ---
    // Retrofit 核心
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    // GSON 转换器 (解析 JSON)
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    // OkHttp (拦截器/日志)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // --- 位置服务 (GPS) ---
    // Google Play Services Location
    implementation("com.google.android.gms:play-services-location:21.3.0")
    // Accompanist Permissions (在 Compose 中优雅处理权限)
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")

    // --- 图片加载 (Image Loading) ---
    // Coil for Compose (用于加载天气图标 URL)
    implementation("io.coil-kt:coil-compose:2.7.0")

    // --- 依赖注入 (Hilt) - 可选但推荐 ---
    // implementation("com.google.dagger:hilt-android:2.51.1")
    // kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    // implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // --- 测试库 (默认保留) ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}