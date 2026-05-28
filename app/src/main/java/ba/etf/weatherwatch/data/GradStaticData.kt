package ba.etf.weatherwatch.data

import ba.etf.weatherwatch.model.Grad

object GradStaticData {
    private val sviGradovi = listOf(
        // BiH
        Grad("Sarajevo", "Bosna i Hercegovina", 43.85, 18.39),
        Grad("Mostar", "Bosna i Hercegovina", 43.34, 17.81),
        Grad("Banja Luka", "Bosna i Hercegovina", 44.77, 17.19),
        Grad("Tuzla", "Bosna i Hercegovina", 44.53, 18.67),
        Grad("Zenica", "Bosna i Hercegovina", 44.20, 17.90),
        // Srbija
        Grad("Beograd", "Srbija", 44.78, 20.44),
        Grad("Novi Sad", "Srbija", 45.25, 19.83),
        Grad("Niš", "Srbija", 43.32, 21.89),
        // Hrvatska
        Grad("Zagreb", "Hrvatska", 45.81, 15.97),
        Grad("Split", "Hrvatska", 43.51, 16.44),
        Grad("Rijeka", "Hrvatska", 45.32, 14.44),
        // Slovenija
        Grad("Ljubljana", "Slovenija", 46.05, 14.50),
        Grad("Maribor", "Slovenija", 46.55, 15.64),
        Grad("Kranj", "Slovenija", 46.23, 14.35),
        // Crna Gora
        Grad("Podgorica", "Crna Gora", 42.43, 19.25),
        Grad("Nikšić", "Crna Gora", 42.78, 18.94),
        Grad("Budva", "Crna Gora", 42.29, 18.84),
        // Sjeverna Makedonija
        Grad("Skoplje", "Sjeverna Makedonija", 41.99, 21.42),
        Grad("Bitolj", "Sjeverna Makedonija", 41.02, 21.33),
        Grad("Ohrid", "Sjeverna Makedonija", 41.11, 20.80)
    )

    fun getGradoviIzDrzave(nazivDrzave: String): List<Grad> {
        return sviGradovi.filter { it.nazivDrzave == nazivDrzave }
    }

    fun getGradoviZaDodavanje(nazivDrzave: String): List<Grad> {
        return getGradoviIzDrzave(nazivDrzave).filter {
            true
        }
    }
}