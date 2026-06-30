package ba.etf.weatherwatch.model.api

import com.google.gson.annotations.SerializedName

data class HourlyData(
    @SerializedName("time") val time: List<String>,
    @SerializedName("temperature_2m") val temperatura: List<Float>,
    @SerializedName("weather_code") val weatherCode: List<Int>,
    @SerializedName("precipitation_probability") val padavinePosto: List<Int>
)
