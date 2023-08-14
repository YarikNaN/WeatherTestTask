package com.example.weathertesttask

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("forecast") val forecast: Forecast
)

data class Forecast(
    @SerializedName("forecastday") val forecastDayList: List<ForecastDay>
)

data class ForecastDay(
    @SerializedName("date") val date: String,
    @SerializedName("day") val day: Day
)

data class Day(
    @SerializedName("condition") val condition: Condition,
    @SerializedName("avgtemp_c") val avgTempC: Double,
    @SerializedName("maxwind_kph") val maxWindKph: Double,
    @SerializedName("avghumidity") val avgHumidity: Double
)

data class Condition(
    @SerializedName("text") val text: String,
    @SerializedName("icon") val icon: String
)
