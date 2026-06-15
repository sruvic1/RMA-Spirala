package ba.etf.weatherwatch.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prognoze")
data class PrognozaEntity(
    @PrimaryKey val nazivLokacije: String,
    val temperatura: Float,
    val osjecajTemperature: Float,
    val opisVremena: String,
    val brzinaVjetra: Float,
    val smjerVjetra: String,
    val uvIndeks: Float,
    val padavine: Float?,
    val vlaznost: Int,
    val pritisak: Int,
    val vidljivost: Int,
    val oblacnost: Int,
    val minTemp: Float,
    val maxTemp: Float,
    val vrijemeTipa: String,
    val prognozaPoSatimaJson: String,
    val prognozaDaniJson: String
)
