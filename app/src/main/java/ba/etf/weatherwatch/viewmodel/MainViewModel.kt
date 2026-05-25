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
    val tipOpcije = listOf("Po satu", "Po danu", "Sedmicno")
    val sveDrzave: List<Drzava> = DrzavaStaticData.getAll()

    private val _filterovaneLokacije = MutableLiveData<List<Lokacija>>()
    val filterovaneLokacije: LiveData<List<Lokacija>> = _filterovaneLokacije

    private val _gradoviZaDrzavu = MutableLiveData<List<Grad>>()
    val gradoviZaDrzavu: LiveData<List<Grad>> = _gradoviZaDrzavu

    private val _dugmeEnabled = MutableLiveData<Boolean>()
    val dugmeEnabled: LiveData<Boolean> = _dugmeEnabled

    private val _odabranaDrzava = MutableLiveData<Drzava?>()
    val odabranaDrzava: LiveData<Drzava?> = _odabranaDrzava

    private val _odabraniGrad = MutableLiveData<Grad?>()
    val odabraniGrad: LiveData<Grad?> = _odabraniGrad

    private val _odabraniTip = MutableLiveData<String?>()
    val odabraniTip: LiveData<String?> = _odabraniTip

    private val _trenutniFilter = MutableLiveData<String>()
    val trenutniFilter: LiveData<String> = _trenutniFilter

    init {
        postaviFilter("Sve moje lokacije")
        _dugmeEnabled.value = false
    }

    fun postaviFilter(filter: String) {
        _trenutniFilter.value = filter
        val lokacije = when (filter) {
            "Sve moje lokacije" -> WeatherStaticData.getLokacijeKorisnika()
            "Sve lokacije" -> WeatherStaticData.getSveLokacije()
            "Vedro" -> {
                WeatherStaticData.getLokacijeKorisnika().filter {
                    WeatherStaticData.getStatus(it.naziv) == "Vedro" || WeatherStaticData.getStatus(it.naziv) == "Toplo"
                }
            }
            "Padavine" -> {
                WeatherStaticData.getLokacijeKorisnika().filter {
                    WeatherStaticData.getStatus(it.naziv) == "Padavine" || WeatherStaticData.getStatus(it.naziv) == "Oluja"
                }
            }
            "Ekstremne temperature" -> {
                WeatherStaticData.getLokacijeKorisnika().filter { it ->
                    val prognoza = WeatherStaticData.getPrognozu(it.naziv)
                    if (prognoza != null) {
                        prognoza.temperatura <= 0f || prognoza.temperatura >= 35f
                    } else {
                        false
                    }
                }
            }
            else -> WeatherStaticData.getLokacijeKorisnika()
        }
        _filterovaneLokacije.value = lokacije
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

        val novaLokacija = Lokacija(
            naziv1 = grad.naziv,
            naziv = grad.naziv as String,
            drzava1 = TODO(),
            drzava = grad.nazivDrzave as Double,
            longitude1 = TODO(),
            latitude = grad.lat as String,
            longitude = grad.lon as Double,
            tipPrikaza = tip,
            korisnikUpisan = true
        )

        WeatherStaticData.addLokaciju(novaLokacija)

        _odabranaDrzava.value = null
        _odabraniGrad.value = null
        _odabraniTip.value = null
        _gradoviZaDrzavu.value = emptyList()
        _dugmeEnabled.value = false

        _trenutniFilter.value?.let { postaviFilter(it) }
    }
}