// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.12.0" apply false
    id("com.android.library") version "8.12.0" apply false

    // 确保 Kotlin 版本是 2.0.0
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false

    // 【新增】Kotlin 2.0 必须添加这个 Compose 编译器插件
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
}