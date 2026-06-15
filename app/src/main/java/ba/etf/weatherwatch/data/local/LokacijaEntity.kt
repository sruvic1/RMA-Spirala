package ba.etf.weatherwatch.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lokacije")
data class LokacijaEntity(
    @PrimaryKey val naziv: String,
    val drzava: String,
    val latitude: Double,
    val longitude: Double,
    val tipPrikaza: String,
    val korisnikUpisan: Boolean
)
