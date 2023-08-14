package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weathertesttask.ForecastDay
import com.squareup.picasso.Picasso

class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    private val weatherList = mutableListOf<ForecastDay>()

    fun setData(data: List<ForecastDay>) {
        weatherList.clear()
        weatherList.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weather, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weather = weatherList[position]
        holder.bind(weather)
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }

    inner class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val conditionTextView: TextView = itemView.findViewById(R.id.conditionTextView)
        private val conditionIconImageView: ImageView = itemView.findViewById(R.id.conditionIconImageView)
        private val tempTextView: TextView = itemView.findViewById(R.id.tempTextView)
        private val windTextView: TextView = itemView.findViewById(R.id.windTextView)
        private val humidityTextView: TextView = itemView.findViewById(R.id.humidityTextView)

        fun bind(forecastDay: ForecastDay) {
            val day = forecastDay.day
            dateTextView.text = forecastDay.date
            conditionTextView.text = day.condition.text
            tempTextView.text = "${day.avgTempC}°C"
            windTextView.text = "Wind: ${day.maxWindKph} kph"
            humidityTextView.text = "Humidity: ${day.avgHumidity}%"

            Picasso.get()
                .load("https:${day.condition.icon}")
                .into(conditionIconImageView)
        }
    }
}

