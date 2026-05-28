package ba.etf.weatherwatch.model

data class AppPostavke (
    val tema: String = "auto", // "light", "dark", "auto"
    val jezik: String = "bs", // "bs", "en"
    val jedinice: String = "celsius", // "celsius", "fahrenheit"
    val notifikacije: Boolean = true,
    val notifikacijeOluja: Boolean = true
)