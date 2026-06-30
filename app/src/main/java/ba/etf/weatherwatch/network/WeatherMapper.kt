package ba.etf.weatherwatch.network

import ba.etf.weatherwatch.model.DnevnaPrognoza
import ba.etf.weatherwatch.model.Prognoza
import ba.etf.weatherwatch.model.SatnaPrognoza
import ba.etf.weatherwatch.model.api.DailyData
import ba.etf.weatherwatch.model.api.HourlyData
import ba.etf.weatherwatch.model.api.OpenMeteoResponse
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object WeatherMapper {

    fun mapirajResponse(naziv: String, response: OpenMeteoResponse): Prognoza {
        val c = response.current
        val daily = response.daily

        val minTemp = daily.minTemp.firstOrNull() ?: c.temperatura
        val maxTemp = daily.maxTemp.firstOrNull() ?: c.temperatura
        val vidljivostKm = (c.vidljivost / 1000f).toInt().coerceAtLeast(0)

        return Prognoza(
            nazivLokacije = naziv,
            temperatura = c.temperatura,
            osjecajTemperature = c.osjecajTemperature,
            opisVremena = wmoUOpis(c.weatherCode),
            brzinaVjetra = c.brzinaVjetra,
            smjerVjetra = stupnjeviUSmjer(c.smjerVjetraStupnjevi),
            uvIndeks = c.uvIndeks,
            padavine = if (c.padavine > 0f) c.padavine else null,
            vlaznost = c.vlaznost,
            pritisak = c.pritisak.toInt(),
            vidljivost = vidljivostKm,
            oblacnost = c.oblacnost,
            minTemp = minTemp,
            maxTemp = maxTemp,
            vrijemeTipa = wmoUVrijemeTip(c.weatherCode),
            prognozaPoSatima = mapirajSatnuPrognozu(response.hourly),
            prognozaDani = mapirajDnevnuPrognozu(daily)
        )
    }

    fun wmoUVrijemeTip(code: Int): String = when (code) {
        0, 1 -> "sunny"
        2 -> "partly_cloudy"
        3 -> "cloudy"
        45, 48 -> "foggy"
        51, 53, 55, 61, 63, 65, 80, 81, 82 -> "rainy"
        71, 73, 75, 77, 85, 86 -> "snowy"
        95, 96, 99 -> "stormy"
        else -> "cloudy"
    }

    fun wmoUOpis(code: Int): String = when (code) {
        0 -> "Vedro i sunčano"
        1 -> "Uglavnom sunčano"
        2 -> "Djelimično oblačno"
        3 -> "Oblačno"
        45 -> "Magleno"
        48 -> "Inja s maglom"
        51 -> "Slaba rosulja"
        53 -> "Umjerena rosulja"
        55 -> "Gusta rosulja"
        61 -> "Slaba kiša"
        63 -> "Umjerena kiša"
        65 -> "Jaka kiša"
        71 -> "Slab snijeg"
        73 -> "Umjeren snijeg"
        75 -> "Jak snijeg"
        77 -> "Snježni zrnci"
        80 -> "Slabi pljuskovi"
        81 -> "Umjereni pljuskovi"
        82 -> "Jaki pljuskovi"
        85 -> "Slabi snježni pljuskovi"
        86 -> "Jaki snježni pljuskovi"
        95 -> "Grmljavinska oluja"
        96 -> "Oluja s blagim gradom"
        99 -> "Oluja s jakim gradom"
        else -> "Pretežno oblačno"
    }

    fun stupnjeviUSmjer(degrees: Int): String = stupnjeviUSmjer(degrees.toFloat())

    fun stupnjeviUSmjer(degrees: Float): String {
        val d = ((degrees % 360) + 360) % 360
        return when {
            d < 22.5 || d >= 337.5 -> "S"
            d < 67.5 -> "SI"
            d < 112.5 -> "I"
            d < 157.5 -> "JI"
            d < 202.5 -> "J"
            d < 247.5 -> "JZ"
            d < 292.5 -> "Z"
            else -> "SZ"
        }
    }

    fun mapirajSatnuPrognozu(hourly: HourlyData): List<SatnaPrognoza> {
        val count = minOf(24, hourly.time.size)
        return (0 until count).map { i ->
            val timeStr = hourly.time[i]
            val sat = if (timeStr.contains("T")) timeStr.substringAfter("T") else timeStr
            SatnaPrognoza(
                sat = sat,
                temperatura = hourly.temperatura[i],
                vrijemeTipa = wmoUVrijemeTip(hourly.weatherCode[i]),
                padavinePostotak = hourly.padavinePosto.getOrElse(i) { 0 }
            )
        }
    }

    fun mapirajDnevnuPrognozu(daily: DailyData): List<DnevnaPrognoza> {
        val count = minOf(7, daily.time.size)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return (0 until count).map { i ->
            val dan = try {
                val date = sdf.parse(daily.time[i])
                val cal = Calendar.getInstance().apply { if (date != null) time = date }
                when (cal.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> "Pon"
                    Calendar.TUESDAY -> "Uto"
                    Calendar.WEDNESDAY -> "Sri"
                    Calendar.THURSDAY -> "Čet"
                    Calendar.FRIDAY -> "Pet"
                    Calendar.SATURDAY -> "Sub"
                    Calendar.SUNDAY -> "Ned"
                    else -> daily.time[i]
                }
            } catch (e: Exception) {
                daily.time[i]
            }
            DnevnaPrognoza(
                dan = dan,
                minTemp = daily.minTemp[i],
                maxTemp = daily.maxTemp[i],
                vrijemeTipa = wmoUVrijemeTip(daily.weatherCode[i]),
                padavinePostotak = daily.padavinePosto.getOrElse(i) { 0 }
            )
        }
    }
}
