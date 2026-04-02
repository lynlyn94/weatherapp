package com.example.weatherapp.data.repository

import com.example.weatherapp.data.api.RetrofitClient
import com.example.weatherapp.data.model.WeatherDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository {

    private val api = RetrofitClient.service

    private val apiKey = "yourkey"

    suspend fun getWeatherData(query: String): WeatherDto? {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getWeather(apiKey, query)
                response
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}