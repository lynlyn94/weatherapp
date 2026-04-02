package com.example.weatherapp.data.model

import com.google.gson.annotations.SerializedName

data class CurrentDto(
    @SerializedName("temp_c") val tempC: Double,
    @SerializedName("condition") val condition: ConditionDto,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("wind_dir") val windDirRaw: String,
    @SerializedName("feelslike_c") val feelsLikeC: Double,
    @SerializedName("uv") val uv: Double,
    @SerializedName("air_quality") val aqi: AirQualityDto?
) {
    val temp: String get() = tempC.toInt().toString()
    val text: String get() = condition.text
    val icon: String get() = condition.code.toString()
    val feelsLike: String get() = feelsLikeC.toInt().toString()
    val uvIndex: String get() = if (uv == 0.0) "0" else String.format("%.1f", uv)

    val aqiLevel: String get() {
        val usEpa = aqi?.usEpaIndex ?: 0
        return when (usEpa) {
            1 -> "优"
            2 -> "良"
            3 -> "轻度污染"
            4 -> "中度污染"
            5 -> "重度污染"
            6 -> "严重污染"
            else -> "未知"
        }
    }

    val windDir: String get() = when (windDirRaw.uppercase()) {
        "N" -> "北风"; "NNE" -> "北东北"; "NE" -> "东北风"; "ENE" -> "东东北"; "E" -> "东风"
        "ESE" -> "东东南"; "SE" -> "东南风"; "SSE" -> "南东南"; "S" -> "南风"; "SSW" -> "南西南"
        "SW" -> "西南风"; "WSW" -> "西西南"; "W" -> "西风"; "WNW" -> "西西北"; "NW" -> "西北风"; "NNW" -> "北西北"
        else -> windDirRaw
    }
}

// 核心基础类：天气状况 (被 CurrentDto, DayDto, HourDto 共同引用)
data class ConditionDto(
    @SerializedName("text") val text: String,
    @SerializedName("code") val code: Int
)

data class AirQualityDto(
    @SerializedName("us-epa-index") val usEpaIndex: Int
)