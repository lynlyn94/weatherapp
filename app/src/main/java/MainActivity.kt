package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.ui.screens.WeatherScreen
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.ui.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 开启 Edge-to-Edge (沉浸式) 体验
        // 这是 Android 15 / API 35 的强烈推荐规范，让内容延伸到状态栏后面
        enableEdgeToEdge()

        setContent {
            // 2. 应用 Material You 主题
            // 所有的颜色、字体设置都在这里生效
            WeatherAppTheme {

                // 3. 创建一个表面容器
                // 它会自动处理背景色（随系统深色/浅色模式变化）
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 4. 获取 ViewModel 实例
                    // 这里使用 Compose 的标准方法 viewModel() 来获取或创建 ViewModel
                    // 它会自动管理生命周期，屏幕旋转数据不会丢失
                    val weatherViewModel: WeatherViewModel = viewModel()

                    // 5. 显示我们的主界面
                    WeatherScreen(viewModel = weatherViewModel)
                }
            }
        }
    }
}