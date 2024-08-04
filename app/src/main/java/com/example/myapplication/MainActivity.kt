package com.example.myapplication

import android.app.DownloadManager.Query
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.color.utilities.ViewingConditions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fetchWeatherData("jaipur")

        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchView2
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query !=null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                return true
            }
        })
    }


    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build()
            .create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName, "efb735f9863803ec0b485a8c78309f6c", "metric")
        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity.toString()
                    val windSpeed = responseBody.wind.speed.toString()
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure.toString()
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemp = responseBody.main.temp_max.toString()
                    val minTemp = responseBody.main.temp_min.toString()

                    binding.textView8.text = "$temperature °C"
                    binding.textView2.text = condition
                    binding.textView4.text = "Max Temp: $maxTemp °C"
                    binding.textView7.text = "Min Temp: $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = windSpeed
                    binding.condition.text = condition
                    binding.sunSet.text = "${time(sunSet)}"
                    binding.sunRise.text = "${time(sunRise)}"
                    binding.seaLevel.text = seaLevel

                    binding.textView5.text = dayName(System.currentTimeMillis())
                    binding.textView9.text = date()
                    binding.textView3.text = cityName

                    changeImageAccordingToWeatherondition(condition)


                } else {
                    Log.d(TAG, "onResponse: Response was not successful")
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}", t)
            }
        })
    }

    private fun changeImageAccordingToWeatherondition(conditions:String) {
        when(conditions){
            "Clouds", "Partly clouds", "Overcast", "Mist", "Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Clear Sky", "Sunny", "Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow", "Moderate Snow", "HeavySnow", "Blizzer"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}


