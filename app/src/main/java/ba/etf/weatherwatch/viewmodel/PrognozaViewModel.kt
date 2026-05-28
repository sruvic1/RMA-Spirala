package ba.etf.weatherwatch.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ba.etf.weatherwatch.data.WeatherStaticData
import ba.etf.weatherwatch.model.Lokacija

class PrognozaViewModel : ViewModel() {
    private val _lokacije = MutableLiveData<List<Lokacija>>()
    val lokacije: LiveData<List<Lokacija>> get() = _lokacije

    fun ucitajLokacije(filter: String) {
        _lokacije.value = when (filter) {
            "Sve moje lokacije" -> WeatherStaticData.getLokacijeKorisnika()
            "Sve lokacije" -> WeatherStaticData.getSveLokacije()
            "Vedro" -> WeatherStaticData.getLokacijePoStatusu("Vedro") + WeatherStaticData.getLokacijePoStatusu("Toplo")
            "Padavine" -> WeatherStaticData.getLokacijePoStatusu("Padavine") + WeatherStaticData.getLokacijePoStatusu("Oluja")
            "Ekstremne temperature" -> WeatherStaticData.getLokacijeKorisnika().filter {
                val p = WeatherStaticData.getPrognozu(it.naziv)
                p != null && (p.temperatura < 0 || p.temperatura > 35)
            }
            else -> WeatherStaticData.getLokacijeKorisnika()
        }
    }
}