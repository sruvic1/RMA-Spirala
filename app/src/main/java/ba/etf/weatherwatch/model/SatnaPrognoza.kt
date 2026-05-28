package ba.etf.weatherwatch.model

data class SatnaPrognoza (
    val sat: String, // format "14:00"
    val temperatura: Float,
    val vrijemeTipa: String,
    val padavinePostotak: Int // vjerovatnoća padavina 0-100
)