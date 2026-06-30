package ba.etf.weatherwatch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ba.etf.weatherwatch.WeatherWatchApplication
import ba.etf.weatherwatch.data.WeatherStaticData
import ba.etf.weatherwatch.model.Lokacija
import ba.etf.weatherwatch.model.Prognoza
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PrognozaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeatherWatchApplication.repository

    private val _filtriraneLokacije = MutableLiveData<List<Lokacija>>(emptyList())
    val filtriraneLokacije: LiveData<List<Lokacija>> get() = _filtriraneLokacije

    private val _prognozeMap = MutableLiveData<Map<String, Prognoza>>(emptyMap())
    val prognozeMap: LiveData<Map<String, Prognoza>> get() = _prognozeMap

    fun ucitajSaFilterom(filter: String) {
        viewModelScope.launch {
            combine(
                repository.getSacuvaneLokacijeFlow(),
                repository.getKesiranjePrognoze()
            ) { lokacije: List<Lokacija>, prognoze: List<Prognoza> ->
                val progMap: Map<String, Prognoza> = prognoze.associateBy { p -> p.nazivLokacije }
                val filtrirane: List<Lokacija> = when (filter) {
                    "Sve moje lokacije" -> lokacije
                    "Sve lokacije" -> {
                        val staticke = WeatherStaticData.getSveLokacije()
                        val mojiNazivi = lokacije.map { lok -> lok.naziv }.toSet()
                        lokacije + staticke.filter { lok -> lok.naziv !in mojiNazivi }
                    }
                    "Vedro" -> lokacije.filter { lok ->
                        progMap[lok.naziv]?.vrijemeTipa in listOf("sunny", "partly_cloudy")
                    }
                    "Padavine" -> lokacije.filter { lok ->
                        progMap[lok.naziv]?.vrijemeTipa in listOf("rainy", "stormy", "snowy")
                    }
                    "Ekstremne temperature" -> lokacije.filter { lok ->
                        val temp = progMap[lok.naziv]?.temperatura
                        temp != null && (temp < 0f || temp > 35f)
                    }
                    else -> lokacije
                }
                Pair(filtrirane, progMap)
            }.collect { pair: Pair<List<Lokacija>, Map<String, Prognoza>> ->
                _filtriraneLokacije.value = pair.first
                _prognozeMap.value = pair.second
            }
        }
    }
}