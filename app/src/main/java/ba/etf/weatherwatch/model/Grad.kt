package ba.etf.weatherwatch.model

data class Drzava(val naziv: String, val kod: String)

data class Grad(val naziv: String, val nazivDrzave: String, val lat: Double, val lon: Double)