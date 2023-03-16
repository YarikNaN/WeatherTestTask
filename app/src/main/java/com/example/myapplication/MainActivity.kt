package com.example.myapplication
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var binEditText: EditText
    private lateinit var getBinInfoButton: Button
    private lateinit var binInfoListView: ListView
    private lateinit var binInfoTextView: TextView
    private lateinit var binInfoAdapter: ArrayAdapter<BinInfo>
    private lateinit var binInfoList: MutableList<BinInfo>
    private lateinit var historyButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binEditText = findViewById(R.id.bin_input)
        getBinInfoButton = findViewById(R.id.lookup_button)
        binInfoListView = findViewById(R.id.binInfoListView)
        historyButton = findViewById(R.id.historyButton)
        binInfoTextView = findViewById(R.id.bin_info)

        // Retrieve history list from shared preferences
        val sharedPreferences = getSharedPreferences("bin_info_history", Context.MODE_PRIVATE)
        val historyString = sharedPreferences.getString("history", null)
        binInfoList = if (historyString != null) {
            Gson().fromJson(historyString, object : TypeToken<MutableList<BinInfo>>() {}.type)
        } else {
            mutableListOf()
        }
        binInfoAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, binInfoList)
        binInfoListView.adapter = binInfoAdapter

        getBinInfoButton.setOnClickListener {
            val bin = binEditText.text.toString()
            val call = BinListApiClient.apiService.getBinInfo(bin)
            call.enqueue(object : Callback<BinInfo> {

                override fun onResponse(call: Call<BinInfo>, response: Response<BinInfo>) {
                    if (response.isSuccessful) {
                        val binInfo = response.body()
                        if (binInfo != null) {
                            // Собираем строку в переменную binInfoString
                            val binInfoString = binInfo.toString()

                            // Заменяем строку "Country" на кликабельную ссылку
                            val countryName = binInfo.country?.name ?: "N/A"
                            val googleMapsUrl = "https://www.google.com/maps/search/?api=1&query=$countryName"
                            val countryLink = "<a href=\"$googleMapsUrl\">$countryName</a>"
                            val binInfoStringWithLink = binInfoString.replace("Country: $countryName", "Country: $countryLink")

                            // Отображаем на экране
                            binInfoTextView.text = Html.fromHtml(binInfoStringWithLink)

                            // Добавляем в список
                            binInfoList.add(binInfo)
                            binInfoAdapter.notifyDataSetChanged()
                            binInfoListView.smoothScrollToPosition(binInfoAdapter.count - 1)
                            saveHistory()
                            Log.d("RESPONSE_BODY", response.body().toString())
                        } else {
                            Toast.makeText(this@MainActivity, "Error: Response body is null", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }

                }


                override fun onFailure(call: Call<BinInfo>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Добавьте следующий код в метод onCreate() вашей активности





        historyButton.setOnClickListener {
            binInfoListView.visibility = if (binInfoListView.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
            historyButton.text = if (binInfoListView.visibility == View.VISIBLE) {
                "Скрыть историю запросов"
            } else {
                "Показать историю запросов"
            }
        }
    }

    private fun saveHistory() {
        val sharedPreferences = getSharedPreferences("bin_info_history", Context.MODE_PRIVATE)
        val historyString = Gson().toJson(binInfoList)
        sharedPreferences.edit().putString("history", historyString).apply()
    }
}

