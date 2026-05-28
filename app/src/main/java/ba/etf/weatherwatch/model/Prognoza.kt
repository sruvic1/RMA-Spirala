package ba.etf.weatherwatch.model

data class Prognoza (
    val nazivLokacije: String,
    val temperatura: Float,
    val osjecajTemperature: Float,
    val opisVremena: String,
    val brzinaVjetra: Float,
    val smjerVjetra: String,
    val uvIndeks: Float,
    val padavine: Float?, // null znači nema padavina
    val vlaznost: Int,
    val pritisak: Int, // u hPa
    val vidljivost: Int, // u km
    val oblacnost: Int, // u %
    val minTemp: Float,
    val maxTemp: Float,
    val vrijemeTipa: String, // "sunny", "cloudy", "rainy", "snowy", "stormy", "foggy", "partly_cloudy"
    val prognozaPoSatima: List<SatnaPrognoza>,
    val prognozaDani: List<DnevnaPrognoza>
)