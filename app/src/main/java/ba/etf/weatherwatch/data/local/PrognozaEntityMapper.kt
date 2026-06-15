package ba.etf.weatherwatch.data.local

import ba.etf.weatherwatch.model.DnevnaPrognoza
import ba.etf.weatherwatch.model.Prognoza
import ba.etf.weatherwatch.model.SatnaPrognoza
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PrognozaEntityMapper {

    private val gson = Gson()

    fun entityUPrognoza(entity: PrognozaEntity): Prognoza {
        val satneType = object : TypeToken<List<SatnaPrognoza>>() {}.type
        val dnevneType = object : TypeToken<List<DnevnaPrognoza>>() {}.type
        val satne: List<SatnaPrognoza> = gson.fromJson(entity.prognozaPoSatimaJson, satneType) ?: emptyList()
        val dnevne: List<DnevnaPrognoza> = gson.fromJson(entity.prognozaDaniJson, dnevneType) ?: emptyList()

        return Prognoza(
            nazivLokacije = entity.nazivLokacije,
            temperatura = entity.temperatura,
            osjecajTemperature = entity.osjecajTemperature,
            opisVremena = entity.opisVremena,
            brzinaVjetra = entity.brzinaVjetra,
            smjerVjetra = entity.smjerVjetra,
            uvIndeks = entity.uvIndeks,
            padavine = entity.padavine,
            vlaznost = entity.vlaznost,
            pritisak = entity.pritisak,
            vidljivost = entity.vidljivost,
            oblacnost = entity.oblacnost,
            minTemp = entity.minTemp,
            maxTemp = entity.maxTemp,
            vrijemeTipa = entity.vrijemeTipa,
            prognozaPoSatima = satne,
            prognozaDani = dnevne
        )
    }

    fun prognozaUEntity(prognoza: Prognoza): PrognozaEntity {
        return PrognozaEntity(
            nazivLokacije = prognoza.nazivLokacije,
            temperatura = prognoza.temperatura,
            osjecajTemperature = prognoza.osjecajTemperature,
            opisVremena = prognoza.opisVremena,
            brzinaVjetra = prognoza.brzinaVjetra,
            smjerVjetra = prognoza.smjerVjetra,
            uvIndeks = prognoza.uvIndeks,
            padavine = prognoza.padavine,
            vlaznost = prognoza.vlaznost,
            pritisak = prognoza.pritisak,
            vidljivost = prognoza.vidljivost,
            oblacnost = prognoza.oblacnost,
            minTemp = prognoza.minTemp,
            maxTemp = prognoza.maxTemp,
            vrijemeTipa = prognoza.vrijemeTipa,
            prognozaPoSatimaJson = gson.toJson(prognoza.prognozaPoSatima),
            prognozaDaniJson = gson.toJson(prognoza.prognozaDani)
        )
    }
}
