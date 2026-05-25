package ba.etf.weatherwatch.model

class Prognoza(
    val nazivLokacije: String,
    val temperatura: Float,
    val osjecajBroj: Float,
    val opisVremena: String,
    val vlaznostZraka: Float,
    val smjerVjetra: String,
    val brzinaVjetra: Float,
    val padavine: Float?, // Znak pitanja rješava "Nothing" problem!
    val pritisak: Int,
    val i1: Int,
    val uvIndeks: Int,
    val i3: Int,
    val minTemp: Float,
    val maxTemp: Float,
    val vrijemeTipa: String,
    val satnePrognoze: List<SatnaPrognoza>,
    val dnevnePrognoze: List<DnevnaPrognoza>
)