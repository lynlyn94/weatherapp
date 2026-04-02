package com.example.weatherapp.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.WeatherRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import com.example.weatherapp.data.local.CityStorage

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    // UI 状态
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _savedCities = MutableStateFlow<List<String>>(emptyList())
    val savedCities: StateFlow<List<String>> = _savedCities.asStateFlow()

    // 1. 加载保存的城市 (在 UI 初始化时调用)
    fun loadSavedCities(context: Context) {
        _savedCities.value = CityStorage.getSavedCities(context)
    }

    // 2. 搜索成功后保存
    fun searchCity(context: Context, cityName: String) { // 增加 Context 参数
        _uiState.update { it.copy(isLoading = true, error = null, isSearchOpen = false) }

        viewModelScope.launch {
            val result = repository.getWeatherData(cityName) // 使用 repository
            if (result != null) {
                val cityToSave = result.location.name
                CityStorage.saveCity(context, cityToSave)

                loadSavedCities(context)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        weatherNow = result.current,
                        weatherDaily = result.forecast.forecastday,
                        weatherHourly = result.forecast.forecastday.firstOrNull()?.hour ?: emptyList(),
                        locationName = result.location.name,
                        error = null
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "未找到该城市，请重试") }
            }
        }
    }

    /**
     * 功能 1: 通过 GPS 定位获取天气 (自动)
     * 特性: 会使用 Geocoder 获取准确的中文位置名
     */
    @SuppressLint("MissingPermission")
    fun refreshWeather(context: Context) {
        _uiState.update { it.copy(isLoading = true, error = null, isSearchOpen = false) }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val cancellationTokenSource = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location: Location? ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude
                Log.d("WeatherVM", "Location found: $lat, $lon")

                viewModelScope.launch {
                    // 1. 先把经纬度转成中文城市名
                    val customCityName = getCityNameFromGeo(context, lat, lon)
                    // 2. 使用 "lat,lon" 格式去请求天气
                    fetchWeatherData(query = "$lat,$lon", customDisplayName = customCityName)
                }
            } else {
                Log.e("WeatherVM", "Location is null, using default")
                // 定位失败，默认查北京
                fetchWeatherData(query = "Beijing", customDisplayName = "北京市")
            }
        }.addOnFailureListener { e ->
            Log.e("WeatherVM", "Location error: ${e.message}")
            _uiState.update { it.copy(isLoading = false, error = "定位失败: ${e.message}") }
        }
    }

    /**
     * 功能 2: 通过城市名搜索 (手动)
     * @param cityName 用户输入的城市名 (支持拼音如 "Nanjing" 或英文)
     */
    fun searchCity(cityName: String) {
        // 关闭搜索框，开始加载
        _uiState.update { it.copy(isLoading = true, error = null, isSearchOpen = false) }
        // 直接用城市名请求
        fetchWeatherData(query = cityName, customDisplayName = null)
    }

    /**
     * 辅助功能: 切换搜索框的显示/隐藏状态
     */
    fun toggleSearch() {
        _uiState.update { it.copy(isSearchOpen = !it.isSearchOpen) }
    }

    /**
     * 核心逻辑: 调用 Repository 获取数据并更新 State
     * @param query API请求参数 ("lat,lon" 或 "CityName")
     * @param customDisplayName 强制显示的城市名。如果为 null，则使用 API 返回的名字
     */
    private fun fetchWeatherData(query: String, customDisplayName: String?) {
        viewModelScope.launch {
            val result = repository.getWeatherData(query)

            if (result != null) {
                // 成功获取数据
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        // 1. 填充实时数据 (含 AQI)
                        weatherNow = result.current,
                        // 2. 填充 5 天预报
                        weatherDaily = result.forecast.forecastday,
                        // 3. 填充 24 小时数据 (取第一天的小时列表)
                        weatherHourly = result.forecast.forecastday.firstOrNull()?.hour ?: emptyList(),
                        // 4. 决定显示的城市名 (优先用 Geocoder 的中文名，没有则用 API 的名字)
                        locationName = customDisplayName ?: result.location.name,
                        error = null
                    )
                }
            } else {
                // 获取失败
                _uiState.update {
                    it.copy(isLoading = false, error = "获取天气失败，请检查网络或拼写")
                }
            }
        }
    }

    /**
     * 工具方法: Geocoder 反向地理编码 (经纬度 -> 中文地址)
     */
    private suspend fun getCityNameFromGeo(context: Context, lat: Double, lon: Double): String {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    // 优先取 locality (市)，没有就取 subAdminArea (区)
                    address.locality ?: address.subAdminArea ?: address.adminArea ?: "未知位置"
                } else {
                    "未知位置"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "定位中..."
            }
        }
    }
}