package ba.etf.weatherwatch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ba.etf.weatherwatch.WeatherWatchApplication
import ba.etf.weatherwatch.data.DrzavaStaticData
import ba.etf.weatherwatch.data.GradStaticData
import ba.etf.weatherwatch.data.WeatherStaticData
import ba.etf.weatherwatch.model.Drzava
import ba.etf.weatherwatch.model.Grad
import ba.etf.weatherwatch.model.Lokacija
import ba.etf.weatherwatch.model.Prognoza
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeatherWatchApplication.repository

    val filterOpcije = listOf("Sve moje lokacije", "Sve lokacije", "Vedro", "Padavine", "Ekstremne temperature")
    val tipOpcije = listOf("Po satu", "Po danu", "Sedmično")
    val sveDrzave: List<Drzava> = DrzavaStaticData.getAll()

    private val _filterovaneLokacije = MutableLiveData<List<Lokacija>>(emptyList())
    val filterovaneLokacije: LiveData<List<Lokacija>> get() = _filterovaneLokacije

    private val _gradoviZaDrzavu = MutableLiveData<List<Grad>>(emptyList())
    val gradoviZaDrzavu: LiveData<List<Grad>> get() = _gradoviZaDrzavu

    private val _dugmeEnabled = MutableLiveData(false)
    val dugmeEnabled: LiveData<Boolean> get() = _dugmeEnabled

    private val _odabranaDrzava = MutableLiveData<Drzava?>()
    val odabranaDrzava: LiveData<Drzava?> get() = _odabranaDrzava

    private val _odabraniGrad = MutableLiveData<Grad?>()
    val odabraniGrad: LiveData<Grad?> get() = _odabraniGrad

    private val _odabraniTip = MutableLiveData<String?>()
    val odabraniTip: LiveData<String?> get() = _odabraniTip

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _greska = MutableLiveData<String?>()
    val greska: LiveData<String?> get() = _greska

    private val _uspjeh = MutableLiveData<String?>()
    val uspjeh: LiveData<String?> get() = _uspjeh

    val brojKesiranih: LiveData<Int> = repository.getBrojKesiranihFlow().asLiveData()

    private val _pronadjenePrognoze = MutableLiveData<Map<String, Prognoza>>(emptyMap())
    val pronadjenePrognoze: LiveData<Map<String, Prognoza>> get() = _pronadjenePrognoze

    private var sacuvaneLok: List<Lokacija> = emptyList()
    private var trenutniFilter = "Sve moje lokacije"

    init {
        viewModelScope.launch {
            repository.getSacuvaneLokacije().collect { lokacije: List<Lokacija> ->
                sacuvaneLok = lokacije
                postaviFilter(trenutniFilter)
            }
        }
    }

    fun postaviFilter(filter: String) {
        trenutniFilter = filter
        val progMap = _pronadjenePrognoze.value ?: emptyMap()
        _filterovaneLokacije.value = when (filter) {
            "Sve moje lokacije" -> sacuvaneLok
            "Sve lokacije" -> {
                val mojeLokacije = sacuvaneLok
                val staticke = WeatherStaticData.getSveLokacije()
                val mojiNazivi = mojeLokacije.map { it.naziv }.toSet()
                mojeLokacije + staticke.filter { it.naziv !in mojiNazivi }
            }
            "Vedro" -> sacuvaneLok.filter { lok ->
                val tip = progMap[lok.naziv]?.vrijemeTipa ?: WeatherStaticData.getStatus(lok.naziv)
                tip in listOf("sunny", "partly_cloudy", "Vedro", "Toplo")
            }
            "Padavine" -> sacuvaneLok.filter { lok ->
                val tip = progMap[lok.naziv]?.vrijemeTipa ?: WeatherStaticData.getStatus(lok.naziv)
                tip in listOf("rainy", "stormy", "snowy", "Padavine", "Oluja")
            }
            "Ekstremne temperature" -> sacuvaneLok.filter { lok ->
                val temp = progMap[lok.naziv]?.temperatura
                    ?: WeatherStaticData.getPrognozu(lok.naziv)?.temperatura
                temp != null && (temp < 0f || temp > 35f)
            }
            else -> sacuvaneLok
        }
    }

    fun osvjeziSveLokacije() {
        if (sacuvaneLok.isEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true
            _greska.value = null
            try {
                val prog = repository.osvjeziSveLokacije(sacuvaneLok)
                _pronadjenePrognoze.value = prog
                postaviFilter(trenutniFilter)
            } catch (e: Exception) {
                _greska.value = "Greška pri učitavanju: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun odaberiDrzavu(drzava: Drzava?) {
        _odabranaDrzava.value = drzava
        _odabraniGrad.value = null
        _gradoviZaDrzavu.value = if (drzava != null) GradStaticData.getGradoviZaDodavanje(drzava.naziv) else emptyList()
        provjeriDugme()
    }

    fun odaberiGrad(grad: Grad?) {
        _odabraniGrad.value = grad
        provjeriDugme()
    }

    fun odaberiTip(tip: String?) {
        _odabraniTip.value = tip
        provjeriDugme()
    }

    private fun provjeriDugme() {
        _dugmeEnabled.value = _odabranaDrzava.value != null &&
                _odabraniGrad.value != null &&
                _odabraniTip.value != null
    }

    fun dodajLokaciju() {
        val grad = _odabraniGrad.value ?: return
        val tip = _odabraniTip.value ?: return
        val nova = Lokacija(grad.naziv, grad.nazivDrzave, grad.lat, grad.lon, tip, true)

        viewModelScope.launch {
            repository.salvaLokaciju(nova)
            _odabranaDrzava.value = null
            _odabraniGrad.value = null
            _odabraniTip.value = null
            _gradoviZaDrzavu.value = emptyList()
            _dugmeEnabled.value = false
            _isLoading.value = true
            _greska.value = null
            try {
                val prog = repository.dohvatiPrognozu(nova)
                val current = _pronadjenePrognoze.value?.toMutableMap() ?: mutableMapOf()
                current[nova.naziv] = prog
                _pronadjenePrognoze.value = current
                postaviFilter(trenutniFilter)
                _uspjeh.value = "Lokacija uspješno dodana!"
            } catch (e: Exception) {
                _greska.value = "Greška: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun ocistiGresku() {
        _greska.value = null
    }

    fun ocistiUspjeh() {
        _uspjeh.value = null
    }
}
