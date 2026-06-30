package ba.etf.weatherwatch

import android.view.View
import android.content.Intent
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ba.etf.weatherwatch.data.local.PrognozaEntityMapper
import ba.etf.weatherwatch.data.local.WeatherDatabase
import ba.etf.weatherwatch.model.DnevnaPrognoza
import ba.etf.weatherwatch.model.Lokacija
import ba.etf.weatherwatch.model.Prognoza
import ba.etf.weatherwatch.model.SatnaPrognoza
import ba.etf.weatherwatch.model.api.CurrentWeather
import ba.etf.weatherwatch.model.api.DailyData
import ba.etf.weatherwatch.model.api.HourlyData
import ba.etf.weatherwatch.model.api.OpenMeteoResponse
import ba.etf.weatherwatch.network.WeatherApiService
import ba.etf.weatherwatch.network.WeatherMapper
import ba.etf.weatherwatch.repository.WeatherRepository
import ba.etf.weatherwatch.ui.SettingsActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TestoviStudentiSpirala2 {

    // -----------------------------------------------------------------------
    // Room in-memory baza — dijele je svi testovi koji trebaju bazu
    // -----------------------------------------------------------------------
    private lateinit var db: WeatherDatabase

    @Before
    fun otvoriDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun zatvoriDb() = db.close()

    // -----------------------------------------------------------------------
    // Pomoćni builderi — izbjegavaju hardkodiranje po cijelom fajlu
    // -----------------------------------------------------------------------

    private fun buildApiResponse(
        weatherCode: Int = 0,
        padavine: Float = 0f,
        satniKodovi: List<Int> = List(24) { 0 },
        dnevniKodovi: List<Int> = List(7) { 0 }
    ) = OpenMeteoResponse(
        current = CurrentWeather(
            temperatura = 20f, osjecajTemperature = 18f,
            padavine = padavine, weatherCode = weatherCode,
            brzinaVjetra = 10f, smjerVjetraStupnjevi = 0,
            vlaznost = 50, pritisak = 1013f, oblacnost = 30, uvIndeks = 2f
        ),
        hourly = HourlyData(
            time = List(24) { i -> "2026-06-13T${"%02d".format(i)}:00" },
            temperatura = List(24) { 20f },
            weatherCode = satniKodovi,
            padavinePosto = List(24) { 0 }
        ),
        daily = DailyData(
            time = List(7) { i -> "2026-06-${13 + i}" },
            weatherCode = dnevniKodovi,
            maxTemp = List(7) { 25f },
            minTemp = List(7) { 10f },
            padavinePosto = List(7) { 0 }
        )
    )

    private fun buildPrognoza(naziv: String = "Sarajevo") = Prognoza(
        nazivLokacije = naziv, temperatura = 22f, osjecajTemperature = 20f,
        opisVremena = "Vedro nebo", brzinaVjetra = 10f, smjerVjetra = "N",
        uvIndeks = 3f, padavine = null, vlaznost = 45, pritisak = 1015,
        vidljivost = 30, oblacnost = 20, minTemp = 8f, maxTemp = 24f,
        vrijemeTipa = "sunny",
        prognozaPoSatima = List(24) { i -> SatnaPrognoza("%02d:00".format(i), 20f, "sunny", 0) },
        prognozaDani = List(7) { i -> DnevnaPrognoza("2026-06-${13 + i}", 8f, 24f, "sunny", 0) }
    )

    private inner class FakeApi(
        private val odgovor: Response<OpenMeteoResponse>? = null,
        private val greska: Exception? = null
    ) : WeatherApiService {
        override suspend fun getForecast(
            latitude: Double, longitude: Double, current: String,
            hourly: String, daily: String, windUnit: String,
            timezone: String, forecastDays: Int
        ): Response<OpenMeteoResponse> {
            greska?.let { throw it }
            return odgovor ?: Response.error(503, "".toResponseBody(null))
        }
    }

    // =======================================================================
    // T01 — WeatherMapper: WMO kodovi pokrivaju sve tipove vremena
    // Spec zadatak 2: wmoUVrijemeTip() mora ispravno mapirati svaki WMO kod
    // =======================================================================
    @Test
    fun t01_wmoUVrijemeTip_svihSedam_tipova() {
        val ocekivano = mapOf(
            0  to "sunny",        1  to "sunny",
            2  to "partly_cloudy",3  to "cloudy",
            45 to "foggy",        48 to "foggy",
            61 to "rainy",        80 to "rainy",
            71 to "snowy",        85 to "snowy",
            95 to "stormy",       99 to "stormy",
            999 to "cloudy"       // nepoznat → default
        )
        ocekivano.forEach { (kod, tip) ->
            Assert.assertEquals("WMO $kod mora biti '$tip'", tip, WeatherMapper.wmoUVrijemeTip(kod))
        }
    }

    // =======================================================================
    // T02 — WeatherMapper: stupnjeviUSmjer daje 8 različitih kompasnih smjerova
    // Spec zadatak 2: stupnjeviUSmjer() za 0°, 45°, 90°... 315° → 8 različitih
    // =======================================================================
    @Test
    fun t02_stupnjeviUSmjer_8razlicitihKompasnih() {
        val smjerovi = listOf(0, 45, 90, 135, 180, 225, 270, 315)
            .map { WeatherMapper.stupnjeviUSmjer(it) }
        Assert.assertEquals(
            "8 kompasnih pravaca mora dati 8 različitih smjerova, dobiveno: $smjerovi",
            8, smjerovi.distinct().size
        )
        // 360° = 0° (normalizacija)
        Assert.assertEquals("360° mora dati isti smjer kao 0°",
            WeatherMapper.stupnjeviUSmjer(0), WeatherMapper.stupnjeviUSmjer(360))
    }

    // =======================================================================
    // T03 — WeatherMapper: satna prognoza — tačno 24 stavke, ispravan format
    // Spec zadatak 2: "Uzima prvih 24 stavki" + "sat = substring nakon 'T'"
    // =======================================================================
    @Test
    fun t03_mapirajSatnuPrognozu_24Stavke_FormatVremena() {
        val response = buildApiResponse()
        val satna = WeatherMapper.mapirajSatnuPrognozu(response.hourly)

        Assert.assertEquals("Mora biti tačno 24 stavke", 24, satna.size)
        // Format: "2026-06-13T14:00" → "14:00"
        satna.forEach { sp ->
            Assert.assertFalse("Sat ne smije sadržavati 'T': '${sp.sat}'", sp.sat.contains('T'))
            Assert.assertTrue("Sat mora biti u formatu HH:MM: '${sp.sat}'", sp.sat.matches(Regex("\\d{2}:\\d{2}")))
        }
    }

    // =======================================================================
    // T04 — WeatherMapper: dnevna prognoza — tačno 7 stavki + vrijemeTipa iz WMO
    // Spec zadatak 2: "Kreira 7 stavki DnevnaPrognoza" + wmoUVrijemeTip()
    // =======================================================================
    @Test
    fun t04_mapirajDnevnuPrognozu_7Stavki_FrijemeTipa() {
        val response = buildApiResponse(dnevniKodovi = listOf(0, 2, 3, 61, 71, 95, 1))
        val dnevna = WeatherMapper.mapirajDnevnuPrognozu(response.daily)

        Assert.assertEquals("Mora biti tačno 7 stavki", 7, dnevna.size)
        Assert.assertEquals("Dan 0: WMO 0 → sunny",        "sunny",        dnevna[0].vrijemeTipa)
        Assert.assertEquals("Dan 2: WMO 3 → cloudy",       "cloudy",       dnevna[2].vrijemeTipa)
        Assert.assertEquals("Dan 3: WMO 61 → rainy",       "rainy",        dnevna[3].vrijemeTipa)
        Assert.assertEquals("Dan 4: WMO 71 → snowy",       "snowy",        dnevna[4].vrijemeTipa)
        Assert.assertEquals("Dan 5: WMO 95 → stormy",      "stormy",       dnevna[5].vrijemeTipa)
    }

    // =======================================================================
    // T05 — WeatherMapper: padavine null kada 0f, nije null kada > 0f
    // Spec zadatak 2: "val padavine = if (current.padavine == 0f) null else current.padavine"
    // =======================================================================
    @Test
    fun t05_mapirajResponse_padavineNullIliVrijednost() {
        val bezPadavina = WeatherMapper.mapirajResponse("TestGrad", buildApiResponse(padavine = 0f))
        Assert.assertNull("padavine mora biti null kada API vrati 0.0", bezPadavina.padavine)

        val saPadavinama = WeatherMapper.mapirajResponse("TestGrad", buildApiResponse(padavine = 3.5f))
        Assert.assertNotNull("padavine ne smije biti null kada API vrati > 0", saPadavinama.padavine)
        Assert.assertEquals("Vrijednost padavina mora biti 3.5", 3.5f, saPadavinama.padavine!!, 0.001f)
    }

    // =======================================================================
    // T06 — PrognozaEntityMapper: roundtrip čuva sve podatke (scalar + liste)
    // Spec zadatak 3.6: prognozaUEntity() ↔ entityUPrognoza() bez gubitaka
    // =======================================================================
    @Test
    fun t06_entityMapper_roundtrip_scalariIListe() {
        val original = buildPrognoza("RoundtripGrad")
        val restored = PrognozaEntityMapper.entityUPrognoza(
            PrognozaEntityMapper.prognozaUEntity(original)
        )

        Assert.assertEquals("nazivLokacije", original.nazivLokacije, restored.nazivLokacije)
        Assert.assertEquals("temperatura", original.temperatura, restored.temperatura, 0.001f)
        Assert.assertEquals("vrijemeTipa", original.vrijemeTipa, restored.vrijemeTipa)
        Assert.assertNull("padavine mora ostati null", restored.padavine)
        // Provjera da su JSON liste korektno serijalizirane i deserijalizirane
        Assert.assertEquals("Broj satnih stavki mora biti očuvan", original.prognozaPoSatima.size, restored.prognozaPoSatima.size)
        Assert.assertEquals("Broj dnevnih stavki mora biti očuvan", original.prognozaDani.size, restored.prognozaDani.size)
        // Provjera formata sata — svaki mora biti HH:MM (bez datuma)
        restored.prognozaPoSatima.forEach { sp ->
            Assert.assertTrue("sat '${sp.sat}' mora biti u formatu HH:MM", sp.sat.matches(Regex("\\d{2}:\\d{2}")))
        }
    }

    // =======================================================================
    // T07 — PrognozaDao: spremi, getByNaziv, REPLACE semantika
    // Spec zadatak 3.3: INSERT OR REPLACE — duplikat ažurira, ne dodaje
    // =======================================================================
    @Test
    fun t07_prognozaDao_spremiGetByNaziv_iReplace() = runBlocking {
        val dao = db.prognozaDao()
        val entity = PrognozaEntityMapper.prognozaUEntity(buildPrognoza("Mostar"))

        dao.spremi(entity)
        val procitano = dao.getByNaziv("Mostar")
        Assert.assertNotNull("getByNaziv mora naći sačuvani entitet", procitano)
        Assert.assertEquals("Temperatura mora biti 22f", 22f, procitano!!.temperatura, 0.001f)

        // REPLACE: isti naziv, nova temperatura
        dao.spremi(entity.copy(temperatura = 30f))
        val azurirano = dao.getByNaziv("Mostar")
        Assert.assertEquals("Temperatura mora biti ažurirana na 30f", 30f, azurirano!!.temperatura, 0.001f)
        Assert.assertEquals("Smije biti samo 1 entitet za 'Mostar'", 1, dao.getBrojKesiranih().first())
    }

    // =======================================================================
    // T08 — PrognozaDao: getBrojKesiranih i obrisiSve (keš management)
    // Spec zadatak 3.3 + 5.3: broj keširanih + brisanje keša
    // =======================================================================
    @Test
    fun t08_prognozaDao_getBrojKesiranih_iObrisiSve() = runBlocking {
        val dao = db.prognozaDao()

        Assert.assertEquals("Početni keš mora biti 0", 0, dao.getBrojKesiranih().first())

        dao.spremi(PrognozaEntityMapper.prognozaUEntity(buildPrognoza("Grad1")))
        dao.spremi(PrognozaEntityMapper.prognozaUEntity(buildPrognoza("Grad2")))
        dao.spremi(PrognozaEntityMapper.prognozaUEntity(buildPrognoza("Grad3")))
        Assert.assertEquals("Nakon 3 upisa keš mora imati 3", 3, dao.getBrojKesiranih().first())

        dao.obrisiSve()
        Assert.assertEquals("Nakon obrisiSve() keš mora biti 0", 0, dao.getBrojKesiranih().first())
        Assert.assertTrue("getAll() mora biti prazna nakon brisanja", dao.getAll().first().isEmpty())
    }

    // =======================================================================
    // T09 — lokacije: perzistiranje i brisanje kroz WeatherRepository
    // Koristi repo.salvaLokaciju() umjesto direktnog LokacijaEntity konstruktora
    // jer spec propisuje opcionalno polje korisnikUpisan koje studenti mogu dodati.
    // Spec zadatak 3.4 + 4.1: salvaLokaciju() + getSacuvaneLokacije()
    // =======================================================================
    @Test
    fun t09_lokacije_salva_getSacuvane_obrisi() = runBlocking {
        val repo = WeatherRepository(FakeApi(), db.prognozaDao(), db.lokacijaDao())
        val dao  = db.lokacijaDao()

        repo.salvaLokaciju(Lokacija("Sarajevo", "BiH", 43.85, 18.41, "Po satu", true))
        repo.salvaLokaciju(Lokacija("Mostar",   "BiH", 43.34, 17.80, "Po danu", true))

        val sve = repo.getSacuvaneLokacije()
        Assert.assertEquals("getSacuvaneLokacije() mora vratiti 2 lokacije", 2, sve.size)
        Assert.assertTrue("Sve lokacije moraju imati korisnikUpisan=true", sve.all { it.korisnikUpisan })

        // Brisanje jedne lokacije
        dao.obrisi("Sarajevo")
        val posleBrisanja = repo.getSacuvaneLokacije()
        Assert.assertEquals("Mora ostati 1 lokacija", 1, posleBrisanja.size)
        Assert.assertEquals("Preostala lokacija mora biti 'Mostar'", "Mostar", posleBrisanja[0].naziv)
    }

    // =======================================================================
    // T10 — WeatherRepository: uspješan API → mapira → sprema u Room
    // Spec zadatak 4.1: "API poziv → mapper → Room → vraća Prognoza"
    // Napomena: test provjerava PONAŠANJE (šta se vrati i šta se spremi u Room),
    // ne KAKO je implementirano interno (WeatherStaticData pozivi nisu testirani).
    // =======================================================================
    @Test
    fun t10_repository_uspjesanApi_spremaURoom() = runBlocking {
        val lokacija = Lokacija("Sarajevo", "BiH", 43.85, 18.41, "Po satu", true)
        val repo = WeatherRepository(
            FakeApi(odgovor = Response.success(buildApiResponse(weatherCode = 1))),
            db.prognozaDao(), db.lokacijaDao()
        )

        val prognoza = repo.dohvatiPrognozu(lokacija)

        Assert.assertNotNull("dohvatiPrognozu() mora vratiti Prognoza pri uspješnom API pozivu", prognoza)
        Assert.assertEquals("naziv mora biti 'Sarajevo'", "Sarajevo", prognoza!!.nazivLokacije)
        // WMO 1 je spec-propisano "sunny" — testira integraciju mapper + repository
        Assert.assertEquals("vrijemeTipa za WMO 1 mora biti 'sunny' (spec zadatak 2)", "sunny", prognoza.vrijemeTipa)
        // Provjera da je prognoza stvarno upisana u Room (keš)
        val uBazi = db.prognozaDao().getByNaziv("Sarajevo")
        Assert.assertNotNull("Prognoza mora biti sačuvana u Room bazi nakon uspješnog API poziva", uBazi)
    }

    // =======================================================================
    // T11 — WeatherRepository: API greška → fallback na Room keš
    // Spec zadatak 4.1: "Ako API failuje (IOException ili !isSuccessful) → čita iz Room-a"
    // Testira i IOException i HTTP grešku (503) jer spec propisuje oba slučaja.
    // =======================================================================
    @Test
    fun t11_repository_apiGreska_fallbackNaRoom() = runBlocking {
        val lokacija = Lokacija("Sarajevo", "BiH", 43.85, 18.41, "Po satu", true)

        // Upiši keširanu prognozu s prepoznatljivom temperaturom
        val kesirana = buildPrognoza("Sarajevo").copy(temperatura = 17f)
        db.prognozaDao().spremi(PrognozaEntityMapper.prognozaUEntity(kesirana))

        // Test 1: IOException (nema interneta)
        val repoIo = WeatherRepository(
            FakeApi(greska = IOException("Nema interneta")),
            db.prognozaDao(), db.lokacijaDao()
        )
        val prognozaIo = repoIo.dohvatiPrognozu(lokacija)
        Assert.assertNotNull("Fallback mora vratiti keširanu prognozu pri IOException", prognozaIo)
        Assert.assertEquals("Temperatura mora doći iz keša (17f)", 17f, prognozaIo!!.temperatura, 0.001f)

        // Test 2: HTTP greška (!response.isSuccessful — 503)
        val repoHttp = WeatherRepository(
            FakeApi(odgovor = Response.error(503, "".toResponseBody(null))),
            db.prognozaDao(), db.lokacijaDao()
        )
        val prognozaHttp = repoHttp.dohvatiPrognozu(lokacija)
        Assert.assertNotNull("Fallback mora vratiti keširanu prognozu pri HTTP grešci", prognozaHttp)
        Assert.assertEquals("Temperatura mora doći iz keša (17f)", 17f, prognozaHttp!!.temperatura, 0.001f)
    }

    // =======================================================================
    // T12 — SettingsActivity: tvBrojKesiranih i btnObrisiKes postoje i vidljivi su
    // Spec zadatak 5.3: keš sekcija u Postavkama
    // =======================================================================
    @Test
    fun t12_settingsActivity_kesSekcija_viewsPostoje() {
        val ctx = ApplicationProvider.getApplicationContext<android.app.Application>()
        ActivityScenario.launch<SettingsActivity>(Intent(ctx, SettingsActivity::class.java))
            .use { scenario ->
                scenario.onActivity { activity ->
                    val tvBroj = activity.findViewById<android.widget.TextView>(R.id.tvBrojKesiranih)
                    val btnObrisi = activity.findViewById<android.widget.Button>(R.id.btnObrisiKes)

                    Assert.assertNotNull("tvBrojKesiranih mora postojati u layoutu", tvBroj)
                    Assert.assertEquals("tvBrojKesiranih mora biti VISIBLE", View.VISIBLE, tvBroj.visibility)
                    Assert.assertFalse("tvBrojKesiranih ne smije biti prazan", tvBroj.text.isNullOrEmpty())

                    Assert.assertNotNull("btnObrisiKes mora postojati u layoutu", btnObrisi)
                    Assert.assertEquals("btnObrisiKes mora biti VISIBLE", View.VISIBLE, btnObrisi.visibility)
                }
            }
    }
}
