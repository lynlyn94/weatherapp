package com.example.weatherapp.data.model

import com.google.gson.annotations.SerializedName

// 1. 总响应
data class WeatherDto(
    @SerializedName("location") val location: LocationDto,
    @SerializedName("current") val current: CurrentDto,
    @SerializedName("forecast") val forecast: ForecastDto
)

data class LocationDto(
    @SerializedName("name") val name: String
)

data class ForecastDto(
    @SerializedName("forecastday") val forecastday: List<ForecastDayDto>
)