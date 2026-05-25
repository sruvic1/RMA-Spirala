package ba.etf.weatherwatch.data

import ba.etf.weatherwatch.model.Lokacija
import ba.etf.weatherwatch.model.Prognoza
import ba.etf.weatherwatch.model.SatnaPrognoza
import ba.etf.weatherwatch.model.DnevnaPrognoza

object WeatherStaticData {

    private val sveLokacije: MutableList<Lokacija> = mutableListOf(
        Lokacija(
            "Sarajevo", "Bosna i Hercegovina", 43.85, 18.39, "Po satu", true,
            longitude = TODO(),
            tipPrikaza = TODO(),
            korisnikUpisan = TODO()
        ),
        Lokacija(
            "Mostar", "Bosna i Hercegovina", 43.34, 17.81, "Po danu", true,
            longitude = TODO(),
            tipPrikaza = TODO(),
            korisnikUpisan = TODO()
        ),
        Lokacija(
            "Banja Luka", "Bosna i Hercegovina", 44.77, 17.19, "Sedmično", true,
            longitude = TODO(),
            tipPrikaza = TODO(),
            korisnikUpisan = TODO()
        ),
        Lokacija(
            "Tuzla", "Bosna i Hercegovina", 44.53, 18.67, "Po satu", false,
            longitude = TODO(),
            tipPrikaza = TODO(),
            korisnikUpisan = TODO()
        ),
        Lokacija(
            "Zenica", "Bosna i Hercegovina", 44.20, 17.90, "Po danu", false,
            longitude = TODO(),
            tipPrikaza = TODO(),
            korisnikUpisan = TODO()
        ),
        Lokacija(
            "Beograd", "Srbija", 44.78, 20.44, "Sedmično", false,
            longitude = TODO(),
            tipPrikaza = TODO(),
            korisnikUpisan = TODO()
        ),
        Lokacija(
            "Zagreb", "Hrvatska", 45.81, 15.97, "Po satu", false,
            longitude = TODO(),
            tipPrikaza = TODO(),
            korisnikUpisan = TODO()
        ),
        Lokacija(
            "Ljubljana", "Slovenija", 46.05, 14.50, "Po danu", false,
            longitude = TODO(),
            tipPrikaza = TODO(),
            korisnikUpisan = TODO()
        ),
        Lokacija(
            "Podgorica", "Crna Gora", 42.43, 19.25, "Sedmično", false,
            longitude = TODO(),
            tipPrikaza = TODO(),
            korisnikUpisan = TODO()
        ),
        Lokacija(
            "Skoplje", "Sjeverna Makedonija", 41.99, 21.43, "Po satu", false,
            longitude = TODO(),
            tipPrikaza = TODO(),
            korisnikUpisan = TODO()
        )
    )

    private fun Lokacija(
        naziv1: String,
        naziv: String,
        drzava1: Double,
        drzava: Double,
        longitude1: String,
        latitude: Boolean,
        longitude: Nothing,
        tipPrikaza: Nothing,
        korisnikUpisan: Nothing
    ): Lokacija {
        return TODO("Provide the return value")
    }

    private fun generisiSatne(): List<SatnaPrognoza> {
        return listOf(
            SatnaPrognoza("08:00", 14f, "partly_cloudy", 10),
            SatnaPrognoza("10:00", 18f, "sunny", 0),
            SatnaPrognoza("12:00", 22f, "sunny", 0),
            SatnaPrognoza("14:00", 25f, "sunny", 0),
            SatnaPrognoza("16:00", 24f, "sunny", 10),
            SatnaPrognoza("18:00", 21f, "partly_cloudy", 20),
            SatnaPrognoza("20:00", 18f, "cloudy", 30),
            SatnaPrognoza("22:00", 16f, "rainy", 70),
            SatnaPrognoza("00:00", 14f, "stormy", 90),
            SatnaPrognoza("02:00", 13f, "rainy", 40)
        )
    }

    private fun generisiDnevne(): List<DnevnaPrognoza> {
        return listOf(
            DnevnaPrognoza("Pon", 12f, 22f, "sunny", 10),
            DnevnaPrognoza("Uto", 14f, 25f, "partly_cloudy", 20),
            DnevnaPrognoza("Sri", 15f, 27f, "sunny", 0),
            DnevnaPrognoza("Čet", 11f, 19f, "rainy", 80),
            DnevnaPrognoza("Pet", 10f, 16f, "stormy", 90),
            DnevnaPrognoza("Sub", 12f, 21f, "cloudy", 30),
            DnevnaPrognoza("Ned", 13f, 24f, "sunny", 10)
        )
    }

    private val prognoze: Map<String, Prognoza> = mapOf(
        "Sarajevo" to Prognoza("Sarajevo", 15f, 14f, "Djelomično oblačno", 15f, "SZ", 4f, null as Float?, 65, 1012, 10, 40, 11f, 22f, "partly_cloudy", generisiSatne(), generisiDnevne()),
        "Mostar" to Prognoza("Mostar", 32f, 35f, "Vedro i vruće", 8f, "J", 8f, null as Float?, 40, 1008, 12, 10, 20f, 34f, "sunny", generisiSatne(), generisiDnevne()),
        "Banja Luka" to Prognoza("Banja Luka", 22f, 22f, "Ugodno i toplo", 12f, "Z", 5f, null as Float?, 55, 1015, 10, 20, 12f, 25f, "sunny", generisiSatne(), generisiDnevne()),
        "Tuzla" to Prognoza("Tuzla", 14f, 13f, "Pljuskovi", 22f, "S", 2f, 4.5f, 85, 1009, 8, 90, 10f, 16f, "rainy", generisiSatne(), generisiDnevne()),
        "Zenica" to Prognoza("Zenica", -2f, -5f, "Mraz i hladno", 5f, "I", 1f, null as Float?, 70, 1022, 9, 5, -5f, 2f, "sunny", generisiSatne(), generisiDnevne()),
        "Beograd" to Prognoza("Beograd", 18f, 18f, "Grmljavinsko nevrijeme", 55f, "JI", 3f, 12.0f, 90, 1002, 6, 95, 12f, 20f, "stormy", generisiSatne(), generisiDnevne()),
        "Zagreb" to Prognoza("Zagreb", 16f, 15f, "Oblačno", 10f, "SZ", 3f, null as Float?, 70, 1014, 10, 80, 11f, 18f, "cloudy", generisiSatne(), generisiDnevne()),
        "Ljubljana" to Prognoza("Ljubljana", 12f, 10f, "Magla", 4f, "Z", 1f, null as Float?, 95, 1018, 2, 100, 8f, 14f, "foggy", generisiSatne(), generisiDnevne()),
        "Podgorica" to Prognoza("Podgorica", 28f, 29f, "Pretežno sunčano", 14f, "SI", 7f, null as Float?, 45, 1011, 11, 15, 18f, 30f, "partly_cloudy", generisiSatne(), generisiDnevne()),
        "Skoplje" to Prognoza("Skoplje", 24f, 24f, "Sunčano", 6f, "J", 6f, null as Float?, 50, 1013, 10, 10, 13f, 26f, "sunny", generisiSatne(), generisiDnevne())
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
            p.padavine != null && p.brzinaVjetra > 50f -> "Oluja"
            p.padavine != null -> "Padavine"
            p.temperatura > 30f || p.uvIndeks > 7 -> "Vruće"
            p.temperatura in 20f..30f -> "Toplo"
            p.temperatura < 0f -> "Mraz"
            else -> "Vedro"
        }
    }

    fun addLokaciju(lokacija: Lokacija) {
        val indeks = sveLokacije.indexOfFirst { it.naziv == lokacija.naziv && it.drzava == lokacija.drzava }
        if (indeks != -1) {
            sveLokacije[indeks] = sveLokacije[indeks].copy(korisnikUpisan = true)
        } else {
            sveLokacije.add(lokacija.copy(korisnikUpisan = true))
        }
    }

    fun dodajLokaciju(novaLokacija: Lokacija) {
        addLokaciju(novaLokacija)
    }
}