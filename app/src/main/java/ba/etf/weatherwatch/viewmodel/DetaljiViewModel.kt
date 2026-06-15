package ba.etf.weatherwatch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ba.etf.weatherwatch.WeatherWatchApplication
import ba.etf.weatherwatch.model.Lokacija
import ba.etf.weatherwatch.model.Prognoza
import kotlinx.coroutines.launch

class DetaljiViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeatherWatchApplication.repository

    private val _prognoza = MutableLiveData<Prognoza?>()
    val prognoza: LiveData<Prognoza?> get() = _prognoza

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _greska = MutableLiveData<String?>()
    val greska: LiveData<String?> get() = _greska

    fun ucitajPrognozu(lokacija: Lokacija) {
        viewModelScope.launch {
            _isLoading.value = true
            _greska.value = null
            try {
                _prognoza.value = repository.dohvatiPrognozu(lokacija)
            } catch (e: Exception) {
                _greska.value = "Greška pri učitavanju: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
