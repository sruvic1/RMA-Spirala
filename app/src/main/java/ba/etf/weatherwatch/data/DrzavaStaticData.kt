package ba.etf.weatherwatch.data

import ba.etf.weatherwatch.model.Drzava

object DrzavaStaticData {
    fun getAll(): List<Drzava> {
        return listOf(
            Drzava("Bosna i Hercegovina", "BA"),
            Drzava("Srbija", "RS"),
            Drzava("Hrvatska", "HR"),
            Drzava("Slovenija", "SI"),
            Drzava("Crna Gora", "ME"),
            Drzava("Sjeverna Makedonija", "MK")
        )
    }
}