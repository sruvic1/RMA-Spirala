package ba.etf.weatherwatch.model

data class Lokacija (
    val naziv: String,
    val drzava: String,
    val latitude: Double,
    val longitude: Double,
    val tipPrikaza: String, // "Po satu", "Po danu", "Sedmično"
    val korisnikUpisan: Boolean = false
)