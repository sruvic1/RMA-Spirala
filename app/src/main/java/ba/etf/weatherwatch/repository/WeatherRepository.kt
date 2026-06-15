package ba.etf.weatherwatch.repository

import ba.etf.weatherwatch.data.local.LokacijaEntity
import ba.etf.weatherwatch.data.local.PrognozaEntityMapper
import ba.etf.weatherwatch.data.local.WeatherDatabase
import ba.etf.weatherwatch.model.Lokacija
import ba.etf.weatherwatch.model.Prognoza
import ba.etf.weatherwatch.network.RetrofitClient
import ba.etf.weatherwatch.network.WeatherMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class WeatherRepository(private val db: WeatherDatabase) {

    private val api = RetrofitClient.service

    suspend fun dohvatiPrognozu(lokacija: Lokacija): Prognoza = withContext(Dispatchers.IO) {
        try {
            val response = api.getForecast(
                latitude = lokacija.latitude,
                longitude = lokacija.longitude,
                current = "temperature_2m,apparent_temperature,weather_code,wind_speed_10m," +
                        "wind_direction_10m,uv_index,relative_humidity_2m,precipitation," +
                        "surface_pressure,cloud_cover,visibility",
                hourly = "temperature_2m,weather_code,precipitation_probability",
                daily = "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max",
                windSpeedUnit = "ms",
                timezone = "auto",
                forecastDays = 7
            )
            val prognoza = WeatherMapper.mapirajResponse(response, lokacija)
            db.prognozaDao().spremi(PrognozaEntityMapper.prognozaUEntity(prognoza))
            prognoza
        } catch (e: Exception) {
            val cached = db.prognozaDao().getByNaziv(lokacija.naziv)
                ?: throw e
            PrognozaEntityMapper.entityUPrognoza(cached)
        }
    }

    suspend fun osvjeziSveLokacije(lokacije: List<Lokacija>): Map<String, Prognoza> = coroutineScope {
        lokacije.map { lok ->
            async(Dispatchers.IO) {
                try {
                    lok.naziv to dohvatiPrognozu(lok)
                } catch (e: Exception) {
                    lok.naziv to null
                }
            }
        }.awaitAll()
            .mapNotNull { (naziv, prog) -> prog?.let { naziv to it } }
            .toMap()
    }

    suspend fun salvaLokaciju(lokacija: Lokacija) = withContext(Dispatchers.IO) {
        db.lokacijaDao().salva(
            LokacijaEntity(
                naziv = lokacija.naziv,
                drzava = lokacija.drzava,
                latitude = lokacija.latitude,
                longitude = lokacija.longitude,
                tipPrikaza = lokacija.tipPrikaza,
                korisnikUpisan = lokacija.korisnikUpisan
            )
        )
    }

    fun getSacuvaneLokacije(): Flow<List<Lokacija>> =
        db.lokacijaDao().getAll().map { entities ->
            entities.map { e ->
                Lokacija(e.naziv, e.drzava, e.latitude, e.longitude, e.tipPrikaza, e.korisnikUpisan)
            }
        }

    fun getKesiranjePrognoze(): Flow<List<Prognoza>> =
        db.prognozaDao().getAll().map { entities ->
            entities.map { PrognozaEntityMapper.entityUPrognoza(it) }
        }

    suspend fun obrisiKes() = withContext(Dispatchers.IO) {
        db.prognozaDao().obrisiSve()
    }

    suspend fun getBrojKesiranih(): Int = withContext(Dispatchers.IO) {
        db.prognozaDao().getBrojKesiranih()
    }

    fun getBrojKesiranihFlow(): Flow<Int> = db.prognozaDao().getBrojKesiranihFlow()
}
