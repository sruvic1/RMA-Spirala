package ba.etf.weatherwatch

import android.app.Application
import ba.etf.weatherwatch.data.local.WeatherDatabase
import ba.etf.weatherwatch.network.RetrofitClient
import ba.etf.weatherwatch.repository.WeatherRepository

class WeatherWatchApplication : Application() {

    companion object {
        lateinit var database: WeatherDatabase
            private set
        lateinit var repository: WeatherRepository
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = WeatherDatabase.getDatabase(this)
        repository = WeatherRepository(
            RetrofitClient.service,
            database.prognozaDao(),
            database.lokacijaDao()
        )
    }
}
