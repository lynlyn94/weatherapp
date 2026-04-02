# 🌤️ Modern Weather App (安卓现代天气应用)

![Android API](https://img.shields.io/badge/API-35%2B-brightgreen.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-blue.svg)
![Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-purple.svg)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-orange.svg)

这是一个基于现代化 Android 技术栈开发的天气预报应用。本项目作为安卓开发课程作业，不仅满足了基础的天气查询与定位需求，还严格遵循了 Google 最新的开发与设计规范。

> **注意**：由于本项目依赖第三方 API，克隆到本地后，需要配置你自己的 API Key 才能正常运行（详见下文「运行指南」）。

---

## ✨ 核心功能 (Features)

- **📍 精准定位**：集成 Google Location Services，自动获取当前设备经纬度（支持无 GPS 信号时的默认城市降级处理）。
- **🌤️ 实时天气**：显示当前温度、体感温度、风向、湿度以及天气图标。
- **📅 5天预报**：列表展示未来 5 天的最高/最低温度及天气变化趋势。
- **🎨 Material You 设计**：
  - 支持 **Dynamic Color (动态取色)**，应用主题色随系统壁纸自动变化（Android 12+）。
  - **Edge-to-Edge (沉浸式全屏)** 体验，状态栏与导航栏透明过渡。
  - 圆角卡片、大号排版等纯正 Material Design 3 视觉风格。
- **🛡️ 权限管理**：使用 Accompanist 优雅处理运行时位置权限请求。

---

## 🛠️ 技术栈 (Tech Stack)

本项目抛弃了传统的 XML 布局，全面拥抱声明式 UI 与响应式编程：

- **开发语言**: Kotlin (2.0.0)
- **UI 框架**: Jetpack Compose
- **设计规范**: Material Design 3 (Material You)
- **架构模式**: MVVM (Model-View-ViewModel) + 单向数据流 (UDF)
- **网络请求**: Retrofit2 + OkHttp3 + Gson
- **异步处理**: Kotlin Coroutines (协程) + StateFlow
- **图片/图标加载**: 本地 SVG 映射 (或 Coil)
- **权限与定位**: Accompanist Permissions + FusedLocationProviderClient

---

## 📂 项目架构 (Architecture)

项目采用清晰的分层结构，确保高内聚、低耦合：

```text
com.example.weatherapp
 ┣ 📂 data         # 数据层
 ┃ ┣ 📂 api        # Retrofit 接口定义 (QWeatherService)
 ┃ ┣ 📂 model      # 数据实体类 (WeatherNow, WeatherDaily)
 ┃ ┗ 📂 repository # 统一数据仓库 (WeatherRepository)
 ┣ 📂 ui           # UI 表现层
 ┃ ┣ 📂 theme      # 主题、颜色、字体配置 (Material 3)
 ┃ ┣ 📂 screens    # Compose 页面 (WeatherScreen)
 ┃ ┗ 📂 viewmodel  # 状态管理与业务逻辑 (WeatherViewModel, UiState)
 ┗ 📂 util         # 工具类 (图标映射 IconMapper)
