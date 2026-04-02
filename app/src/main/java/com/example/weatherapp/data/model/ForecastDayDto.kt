package com.example.weatherapp.data.model

import com.google.gson.annotations.SerializedName

// 预报天 (包含了 hour 列表)
data class ForecastDayDto(
    @SerializedName("date") val date: String,
    @SerializedName("day") val day: DayDto,
    @SerializedName("hour") val hour: List<HourDto> // 24小时预报数据
) {
    val tempMax: String get() = day.maxtempC.toInt().toString()
    val tempMin: String get() = day.mintempC.toInt().toString()
    val textDay: String get() = day.condition.text
    val iconDay: String get() = day.condition.code.toString()
}

data class DayDto(
    @SerializedName("maxtemp_c") val maxtempC: Double,
    @SerializedName("mintemp_c") val mintempC: Double,
    @SerializedName("condition") val condition: ConditionDto
)

data class HourDto(
    @SerializedName("time") val time: String, // 格式 "2023-12-09 14:00"
    @SerializedName("temp_c") val tempC: Double,
    @SerializedName("condition") val condition: ConditionDto
) {
    val temp: String get() = tempC.toInt().toString()
    val icon: String get() = condition.code.toString()
    // 截取时间字符串，只显示 "14:00"
    val timeOnly: String get() = time.split(" ").lastOrNull() ?: time
}