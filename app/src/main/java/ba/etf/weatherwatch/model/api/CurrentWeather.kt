package ba.etf.weatherwatch.model.api

import com.google.gson.annotations.SerializedName

data class CurrentWeather(
    @SerializedName("temperature_2m") val temperatura: Float,
    @SerializedName("apparent_temperature") val osjecajTemperature: Float,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("wind_speed_10m") val brzinaVjetra: Float,
    @SerializedName("wind_direction_10m") val smjerVjetraStupnjevi: Int,
    @SerializedName("uv_index") val uvIndeks: Float,
    @SerializedName("relative_humidity_2m") val vlaznost: Int,
    @SerializedName("precipitation") val padavine: Float,
    @SerializedName("surface_pressure") val pritisak: Float,
    @SerializedName("cloud_cover") val oblacnost: Int,
    @SerializedName("visibility") val vidljivost: Float = 0f
)
