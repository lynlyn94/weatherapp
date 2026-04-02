package com.example.weatherapp.data.local

import android.content.Context
import android.content.SharedPreferences

object CityStorage {
    private const val PREF_NAME = "weather_city_prefs"
    private const val KEY_CITIES = "saved_cities"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // 获取所有保存的城市
    fun getSavedCities(context: Context): List<String> {
        val prefs = getPrefs(context)
        val citiesString = prefs.getString(KEY_CITIES, "") ?: ""
        if (citiesString.isEmpty()) return emptyList()
        return citiesString.split(",").filter { it.isNotEmpty() }
    }

    // 保存新城市
    fun saveCity(context: Context, cityName: String) {
        val currentCities = getSavedCities(context).toMutableList()
        if (!currentCities.contains(cityName)) {
            currentCities.add(0, cityName) // 加到最前面
            val prefs = getPrefs(context)
            prefs.edit().putString(KEY_CITIES, currentCities.joinToString(",")).apply()
        }
    }

}