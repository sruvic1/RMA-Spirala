package ba.etf.weatherwatch.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ba.etf.weatherwatch.model.AppPostavke

class SettingsViewModel : ViewModel() {
    private val _postavke = MutableLiveData<AppPostavke>().apply { value = AppPostavke() }
    val postings: LiveData<AppPostavke> get() = _postavke

    fun postaviTemu(tema: String) {
        _postavke.value = _postavke.value?.copy(tema = tema)
    }
    fun postaviJezik(jezik: String) {
        _postavke.value = _postavke.value?.copy(jezik = jezik)
    }
    fun postaviJedinice(jedinice: String) {
        _postavke.value = _postavke.value?.copy(jedinice = jedinice)
    }
    fun postaviNotifikacije(v: Boolean) {
        _postavke.value = _postavke.value?.copy(notifikacije = v)
    }
    fun postaviNotifikacijeOluja(v: Boolean) {
        _postavke.value = _postavke.value?.copy(notifikacijeOluja = v)
    }
}