package ba.etf.weatherwatch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import ba.etf.weatherwatch.WeatherWatchApplication
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeatherWatchApplication.repository

    val brojKesiranih: LiveData<Int> = repository.getBrojKesiranihFlow().asLiveData()

    fun obrisiKes() {
        viewModelScope.launch {
            repository.obrisiKes()
        }
    }
}
