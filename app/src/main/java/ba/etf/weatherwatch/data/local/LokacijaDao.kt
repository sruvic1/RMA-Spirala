package ba.etf.weatherwatch.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LokacijaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun salva(lokacija: LokacijaEntity)

    @Query("SELECT * FROM lokacije")
    fun getAll(): Flow<List<LokacijaEntity>>

    @Query("DELETE FROM lokacije WHERE naziv = :naziv")
    suspend fun obrisi(naziv: String)
}
