package ba.etf.weatherwatch.model

data class DnevnaPrognoza (
    val dan: String, // "Pon", "Uto", "Sri", "Cet", "Pet", "Sub", "Ned"
    val minTemp: Float,
    val maxTemp: Float,
    val vrijemeTipa: String,
    val padavinePostotak: Int
)