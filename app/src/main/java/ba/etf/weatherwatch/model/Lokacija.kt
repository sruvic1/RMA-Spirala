package ba.etf.weatherwatch.model

data class Lokacija(
    val naziv1: Any,
    val naziv: String,
    val drzava1: Any,
    val drzava: Double,
    val longitude1: Any,
    val latitude: String,
    val longitude: Double,
    val tipPrikaza: String,
    val korisnikUpisan: Boolean = false
)