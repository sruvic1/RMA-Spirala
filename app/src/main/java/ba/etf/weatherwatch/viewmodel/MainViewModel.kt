package ba.etf.weatherwatch.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ba.etf.weatherwatch.data.DrzavaStaticData
import ba.etf.weatherwatch.data.GradStaticData
import ba.etf.weatherwatch.data.WeatherStaticData
import ba.etf.weatherwatch.model.Drzava
import ba.etf.weatherwatch.model.Grad
import ba.etf.weatherwatch.model.Lokacija

class MainViewModel : ViewModel() {
    val filterOpcije = listOf("Sve moje lokacije", "Sve lokacije", "Vedro", "Padavine", "Ekstremne temperature")
    val tipOpcije = listOf("Po satu", "Po danu", "Sedmično")
    val sveDrzave: List<Drzava> = DrzavaStaticData.getAll()

    private val _filterovaneLokacije = MutableLiveData<List<Lokacija>>()
    val filterovaneLokacije: LiveData<List<Lokacija>> get() = _filterovaneLokacije

    private val _gradoviZaDrzavu = MutableLiveData<List<Grad>>()
    val gradoviZaDrzavu: LiveData<List<Grad>> get() = _gradoviZaDrzavu

    private val _dugmeEnabled = MutableLiveData<Boolean>()
    val dugmeEnabled: LiveData<Boolean> get() = _dugmeEnabled

    private val _odabranaDrzava = MutableLiveData<Drzava?>()
    val odabranaDrzava: LiveData<Drzava?> get() = _odabranaDrzava

    private val _odabraniGrad = MutableLiveData<Grad?>()
    val odabraniGrad: LiveData<Grad?> get() = _odabraniGrad

    private val _odabraniTip = MutableLiveData<String?>()
    val odabraniTip: LiveData<String?> get() = _odabraniTip

    private var trenutniFilter = "Sve moje lokacije"

    init {
        postaviFilter(trenutniFilter)
        _dugmeEnabled.value = false
    }

    fun postaviFilter(filter: String) {
        trenutniFilter = filter
        _filterovaneLokacije.value = when (filter) {
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

    fun odaberiDrzavu(drzava: Drzava?) {
        _odabranaDrzava.value = drzava
        _odabraniGrad.value = null
        if (drzava != null) {
            _gradoviZaDrzavu.value = GradStaticData.getGradoviZaDodavanje(drzava.naziv)
        } else {
            _gradoviZaDrzavu.value = emptyList()
        }
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
        _dugmeEnabled.value = _odabranaDrzava.value != null && _odabraniGrad.value != null && _odabraniTip.value != null
    }

    fun dodajLokaciju() {
        val grad = _odabraniGrad.value ?: return
        val tip = _odabraniTip.value ?: return

        val nova = Lokacija(grad.naziv, grad.nazivDrzave, grad.lat, grad.lon, tip, true)
        WeatherStaticData.dodajLokaciju(nova)

        _odabranaDrzava.value = null
        _odabraniGrad.value = null
        _odabraniTip.value = null
        _gradoviZaDrzavu.value = emptyList()
        _dugmeEnabled.value = false
        postaviFilter(trenutniFilter)
    }
}