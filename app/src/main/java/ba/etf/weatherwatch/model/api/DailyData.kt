package ba.etf.weatherwatch.model.api

import com.google.gson.annotations.SerializedName

data class DailyData(
    @SerializedName("time") val time: List<String>,
    @SerializedName("weather_code") val weatherCode: List<Int>,
    @SerializedName("temperature_2m_max") val maxTemp: List<Float>,
    @SerializedName("temperature_2m_min") val minTemp: List<Float>,
    @SerializedName("precipitation_probability_max") val padavinePosto: List<Int>
)
