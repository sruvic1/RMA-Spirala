package ba.etf.weatherwatch.data

import ba.etf.weatherwatch.model.DnevnaPrognoza
import ba.etf.weatherwatch.model.Lokacija
import ba.etf.weatherwatch.model.Prognoza
import ba.etf.weatherwatch.model.SatnaPrognoza

object WeatherStaticData {
    private val sveLokacije = mutableListOf(
        Lokacija("Sarajevo", "Bosna i Hercegovina", 43.85, 18.39, "Po satu", true),
        Lokacija("Mostar", "Bosna i Hercegovina", 43.34, 17.81, "Po danu", true),
        Lokacija("Banja Luka", "Bosna i Hercegovina", 44.77, 17.19, "Sedmično", true),
        Lokacija("Tuzla", "Bosna i Hercegovina", 44.53, 18.67, "Po satu", false),
        Lokacija("Zenica", "Bosna i Hercegovina", 44.20, 17.90, "Po danu", false),
        Lokacija("Beograd", "Srbija", 44.78, 20.44, "Po satu", false),
        Lokacija("Zagreb", "Hrvatska", 45.81, 15.97, "Po danu", false),
        Lokacija("Ljubljana", "Slovenija", 46.05, 14.50, "Sedmično", false),
        Lokacija("Podgorica", "Crna Gora", 42.43, 19.25, "Po satu", false),
        Lokacija("Skoplje", "Sjeverna Makedonija", 41.99, 21.42, "Po danu", false)
    )

    // Mock satna i dnevna lista prognoza
    private val mockSati = List(10) { SatnaPrognoza("${10 + it}:00", 22f + it, "sunny", 10) }
    private val mockDani = listOf("Pon", "Uto", "Sri", "Cet", "Pet", "Sub", "Ned").map { DnevnaPrognoza(it, 12f, 28f, "sunny", 20) }

    private val prognoze = mapOf(
        "Sarajevo" to Prognoza("Sarajevo", 24f, 23f, "Sunčano", 12f, "NE", 5f, null, 40, 1013, 10, 10, 12f, 28f, "sunny", mockSati, mockDani),
        "Mostar" to Prognoza("Mostar", 32f, 34f, "Vruće i vedro", 8f, "S", 8f, null, 30, 1011, 10, 5, 20f, 36f, "sunny", mockSati, mockDani),
        "Banja Luka" to Prognoza("Banja Luka", -2f, -5f, "Mraz i hladno", 15f, "N", 1f, null, 80, 1020, 8, 20, -5f, 2f, "cloudy", mockSati, mockDani),
        "Beograd" to Prognoza("Beograd", 18f, 18f, "Pljusak sa grmljavinom", 55f, "W", 2f, 12.5f, 90, 1005, 5, 95, 10f, 20f, "stormy", mockSati, mockDani),
        "Zagreb" to Prognoza("Zagreb", 15f, 14f, "Kišovito", 22f, "NW", 3f, 4.2f, 85, 1008, 7, 80, 9f, 17f, "rainy", mockSati, mockDani)
    )

    fun getLokacijeKorisnika(): List<Lokacija> {
        return sveLokacije.filter { it.korisnikUpisan }.sortedBy { it.naziv }
    }

    fun getSveLokacije(): List<Lokacija> {
        return sveLokacije.sortedBy { it.naziv }
    }

    fun getLokacijePoStatusu(status: String): List<Lokacija> {
        return getLokacijeKorisnika().filter { getStatus(it.naziv) == status }
    }

    fun getPrognozu(nazivLokacije: String): Prognoza? {
        return prognoze[nazivLokacije]
    }

    fun getStatus(nazivLokacije: String): String {
        val p = prognoze[nazivLokacije] ?: return "Vedro"
        return when {
            p.padavine != null && p.brzinaVjetra > 50 -> "Oluja"
            p.padavine != null -> "Padavine"
            p.temperatura > 30 || p.uvIndeks > 7 -> "Vruće"
            p.temperatura in 20.0..30.0 -> "Toplo"
            p.temperatura < 0 -> "Mraz"
            else -> "Vedro"
        }
    }

    fun dodajLokaciju(lokacija: Lokacija) {
        val index = sveLokacije.indexOfFirst { it.naziv == lokacija.naziv && it.drzava == lokacija.drzava }
        if (index != -1) {
            sveLokacije[index] = sveLokacije[index].copy(korisnikUpisan = true)
        } else {
            sveLokacije.add(lokacija.copy(korisnikUpisan = true))
        }
    }
}