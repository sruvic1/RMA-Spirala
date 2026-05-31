package ba.etf.weatherwatch

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity // VAŽAN IMPORT ZA NOVIJI SDK!
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import ba.etf.weatherwatch.data.GradStaticData
import ba.etf.weatherwatch.data.WeatherStaticData
import ba.etf.weatherwatch.model.Lokacija
import ba.etf.weatherwatch.ui.DetaljiActivity
import ba.etf.weatherwatch.ui.MainActivity
import ba.etf.weatherwatch.ui.PrognozaActivity
import ba.etf.weatherwatch.ui.SettingsActivity
import org.hamcrest.CoreMatchers.*
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherWatchSpirala1Testovi {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private val ctx   get() = ApplicationProvider.getApplicationContext<android.app.Application>()
    private val prefs get() = ctx.getSharedPreferences("ww_prefs", Context.MODE_PRIVATE)

    @Before fun initIntents() { Intents.init() }

    @After fun tearDown() {
        prefs.edit().putString("jedinice", "celsius").putString("tema", "auto").commit()
        Intents.release()
    }

    // --- Data sloj ---

    @Test
    fun t01_getLokacijeKorisnika_vraca_minimum3() {
        val lokacije = WeatherStaticData.getLokacijeKorisnika()
        Assert.assertTrue(
            "getLokacijeKorisnika() mora vratiti >= 3, dobiveno: ${lokacije.size}",
            lokacije.size >= 3
        )
    }

    @Test
    fun t02_getStatus_nepostojeci_grad_vraca_Vedro() {
        Assert.assertEquals("Vedro", WeatherStaticData.getStatus("__NEMA_XYZ_999__"))
    }

    @Test
    fun t03_dodajLokaciju_novi_povecava_za1_i_korisnikUpisanTrue() {
        val pocetni   = WeatherStaticData.getLokacijeKorisnika().size
        val testNaziv = "TestGrad_${System.currentTimeMillis()}"
        WeatherStaticData.dodajLokaciju(Lokacija(testNaziv, "TestDrzava", 0.0, 0.0, "Po satu"))
        val nova = WeatherStaticData.getLokacijeKorisnika()
        Assert.assertEquals("dodajLokaciju() mora povecati listu za 1", pocetni + 1, nova.size)
        Assert.assertTrue(
            "Dodana lokacija mora imati korisnikUpisan=true",
            nova.find { it.naziv == testNaziv }!!.korisnikUpisan
        )
    }

    @Test
    fun t04_dodajLokaciju_duplikat_sarajeva_ne_dodaje_novu_stavku() {
        val pocetni = WeatherStaticData.getSveLokacije().size
        WeatherStaticData.dodajLokaciju(Lokacija("Sarajevo", "Bosna i Hercegovina", 43.85, 18.39, "Po satu"))
        Assert.assertEquals(
            "Duplikat NE smije povecati getSveLokacije().size",
            pocetni, WeatherStaticData.getSveLokacije().size
        )
    }

    // --- MainActivity UI flow ---

    @Test
    fun t05_dodajDugme_disabled_dok_nisu_odabrana_sva_tri_spinnera() {
        onView(withId(R.id.dodajLokacijuDugme)).check(matches(isNotEnabled()))

        onView(withId(R.id.odabirDrzave)).perform(click())
        onData(allOf(instanceOf(String::class.java), `is`("Srbija"))).perform(click())
        onView(withId(R.id.dodajLokacijuDugme)).check(matches(isNotEnabled()))

        onView(withId(R.id.odabirGrada)).perform(click())
        onData(anything()).atPosition(1).perform(click())
        onView(withId(R.id.dodajLokacijuDugme)).check(matches(isNotEnabled()))

        onView(withId(R.id.odabirTipaPrikaza)).perform(click())
        onData(anything()).atPosition(1).perform(click())

        onView(withId(R.id.dodajLokacijuDugme)).check(matches(isEnabled()))
    }

    // --- Fahrenheit / Celsius ---

    @Test
    fun t06_detaljiActivity_fahrenheit_prikaz_sadrzi_F() {
        prefs.edit().putString("jedinice", "fahrenheit").commit()
        val intent = Intent(ctx, DetaljiActivity::class.java).apply {
            putExtra("LOKACIJA", "Sarajevo")
        }
        // UKLONJENO RUŠENJE: launchActivity eksplicitno govori tip i rješava grešku 115
        val scenario = launchActivity<DetaljiActivity>(intent)
        onView(withId(R.id.tvGlavnaTemp)).check(matches(withText(containsString("F"))))
        scenario.close()
    }

    @Test
    fun t07_detaljiActivity_celsius_prikaz_ne_sadrzi_F() {
        prefs.edit().putString("jedinice", "celsius").commit()
        val intent = Intent(ctx, DetaljiActivity::class.java).apply {
            putExtra("LOKACIJA", "Sarajevo")
        }
        // UKLONJENO RUŠENJE: launchActivity eksplicitno govori tip i rješava grešku 127
        val scenario = launchActivity<DetaljiActivity>(intent)
        onView(withId(R.id.tvGlavnaTemp)).check(matches(not(withText(containsString("F")))))
        scenario.close()
    }

    // --- Settings SharedPreferences ---

    @Test
    fun t08_settings_rbFahrenheit_cuva_fahrenheit_u_prefs() {
        prefs.edit().putString("jedinice", "celsius").commit()
        val intent = Intent(ctx, SettingsActivity::class.java)
        // UKLONJENO RUŠENJE: launchActivity eksplicitno govori tip i rješava grešku 139
        val scenario = launchActivity<SettingsActivity>(intent)
        onView(withId(R.id.rbFahrenheit)).perform(click())
        Assert.assertEquals("fahrenheit", prefs.getString("jedinice", "celsius"))
        scenario.close()
    }

    @Test
    fun t09_settings_rbTemaLight_cuva_light_u_prefs() {
        prefs.edit().putString("tema", "auto").commit()
        val intent = Intent(ctx, SettingsActivity::class.java)
        // UKLONJENO RUŠENJE: launchActivity eksplicitno govori tip i rješava grešku 150
        val scenario = launchActivity<SettingsActivity>(intent)
        onView(withId(R.id.rbTemaLight)).perform(click())
        Assert.assertEquals("light", prefs.getString("tema", "auto"))
        scenario.close()
    }

    // --- Navigacija ---

    @Test
    fun t10_menuPostavke_otvara_settingsActivity() {
        onView(withId(R.id.action_settings)).perform(click())
        Intents.intended(hasComponent(SettingsActivity::class.java.name))
    }

    @Test
    fun t11_prikaziPrognozuDugme_otvara_prognozaActivity_sa_FILTER_extrom() {
        onView(withId(R.id.prikaziPrognozuDugme)).perform(click())
        Intents.intended(allOf(
            hasComponent(PrognozaActivity::class.java.name),
            hasExtraWithKey("FILTER")
        ))
    }

    @Test
    fun t12_klikNaRecyclerView_otvara_detaljiActivity_sa_LOKACIJA_extrom() {
        onView(withId(R.id.recyclerLokacije))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        Intents.intended(allOf(
            hasComponent(DetaljiActivity::class.java.name),
            hasExtraWithKey("LOKACIJA")
        ))
    }



    @Test
    fun t13_dodajGradPutemUI_pojavljuje_se_u_recyclerViewu() {
        val dostupni = GradStaticData.getGradoviZaDodavanje("Srbija")
        Assume.assumeTrue("Srbija mora imati dostupnih gradova za dodavanje", dostupni.isNotEmpty())
        val ocekivaniNaziv = dostupni.first().naziv

        onView(withId(R.id.odabirDrzave)).perform(click())
        onData(allOf(instanceOf(String::class.java), `is`("Srbija"))).perform(click())

        onView(withId(R.id.odabirGrada)).perform(click())
        onData(anything()).atPosition(1).perform(click())

        onView(withId(R.id.odabirTipaPrikaza)).perform(click())
        onData(anything()).atPosition(1).perform(click())

        onView(withId(R.id.dodajLokacijuDugme)).check(matches(isEnabled()))
        onView(withId(R.id.dodajLokacijuDugme)).perform(click())

        Assert.assertTrue(
            "$ocekivaniNaziv mora biti u getLokacijeKorisnika() nakon dodavanja putem UI-ja",
            WeatherStaticData.getLokacijeKorisnika().any { it.naziv == ocekivaniNaziv }
        )
        onView(withId(R.id.recyclerLokacije))
            .check(matches(hasDescendant(withText(ocekivaniNaziv))))
    }
}