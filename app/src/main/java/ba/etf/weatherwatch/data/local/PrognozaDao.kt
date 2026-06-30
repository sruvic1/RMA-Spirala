package ba.etf.weatherwatch.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PrognozaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun spremi(prognoza: PrognozaEntity)

    @Query("SELECT * FROM prognoze WHERE nazivLokacije = :naziv LIMIT 1")
    suspend fun getByNaziv(naziv: String): PrognozaEntity?

    @Query("SELECT * FROM prognoze")
    fun getAll(): Flow<List<PrognozaEntity>>

    @Query("DELETE FROM prognoze WHERE nazivLokacije = :naziv")
    suspend fun obrisi(naziv: String)

    @Query("DELETE FROM prognoze")
    suspend fun obrisiSve()

    @Query("SELECT COUNT(*) FROM prognoze")
    fun getBrojKesiranih(): Flow<Int>
}
