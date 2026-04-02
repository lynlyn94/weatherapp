package com.example.weatherapp.ui.viewmodel

import com.example.weatherapp.data.model.CurrentDto
import com.example.weatherapp.data.model.ForecastDayDto
import com.example.weatherapp.data.model.HourDto

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weatherNow: CurrentDto? = null,
    val weatherDaily: List<ForecastDayDto> = emptyList(),
    // 新增：24小时预报列表 (我们只取当天的后续小时)
    val weatherHourly: List<HourDto> = emptyList(),
    val error: String? = null,
    val locationName: String = "定位中...",
    // 新增：搜索框是否显示
    val isSearchOpen: Boolean = false
)