package ba.etf.weatherwatch.model.api

import com.google.gson.annotations.SerializedName

data class CurrentWeather(
    @SerializedName("temperature_2m") val temperature: Float,
    @SerializedName("apparent_temperature") val apparentTemperature: Float,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("wind_speed_10m") val windSpeed: Float,
    @SerializedName("wind_direction_10m") val windDirection: Float,
    @SerializedName("uv_index") val uvIndex: Float,
    @SerializedName("relative_humidity_2m") val humidity: Int,
    @SerializedName("precipitation") val precipitation: Float,
    @SerializedName("surface_pressure") val pressure: Float,
    @SerializedName("cloud_cover") val cloudCover: Int,
    @SerializedName("visibility") val visibility: Float
)
