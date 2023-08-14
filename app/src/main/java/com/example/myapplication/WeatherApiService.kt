package com.example.myapplication

import com.example.weathertesttask.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/forecast.json")
    fun getWeatherForecast(
        @Query("q") city: String,
        @Query("days") days: Int,
        @Query("key") apiKey: String
    ): Call<WeatherResponse>
}
