package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weathertesttask.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val apiKey = "af3999ef32664927875125626231108"
    private val city = "moscow"
    private val days = 5

    private lateinit var recyclerView: RecyclerView
    private lateinit var weatherAdapter: WeatherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        weatherAdapter = WeatherAdapter()
        recyclerView.adapter = weatherAdapter

        fetchWeatherData()
    }

    private fun fetchWeatherData() {
        val call = RetrofitClient.apiService.getWeatherForecast(city, days, apiKey)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val forecastData = response.body()?.forecast?.forecastDayList ?: emptyList()
                    weatherAdapter.setData(forecastData)
                    Log.d("MainActivity", "Weather data fetched successfully. Count: ${forecastData.size}")
                } else {
                    Log.e("MainActivity", "Weather data request failed. Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("MainActivity", "Weather data request failed.", t)
            }
        })
    }
}


