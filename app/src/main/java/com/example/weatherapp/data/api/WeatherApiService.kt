package com.example.weatherapp.data.api

import com.example.weatherapp.data.model.WeatherDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    // WeatherAPI
    @GET("forecast.json")
    suspend fun getWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String, // 格式: "lat,lon"
        @Query("days") days: Int = 5,
        @Query("aqi") aqi: String = "yes",
        @Query("lang") lang: String = "zh"
    ): WeatherDto
}