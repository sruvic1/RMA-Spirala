package ba.etf.weatherwatch.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ba.etf.weatherwatch.R
import ba.etf.weatherwatch.data.WeatherStaticData
import ba.etf.weatherwatch.model.Lokacija
import ba.etf.weatherwatch.model.Prognoza

class LokacijaAdapter(private val onClick: (Lokacija) -> Unit) :
    ListAdapter<Lokacija, LokacijaAdapter.ViewHolder>(DiffCallback) {

    var fahrenheit: Boolean = false

    @SuppressLint("NotifyDataSetChanged")
    var prognozeMap: Map<String, Prognoza> = emptyMap()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val statusIndikator: ImageView = view.findViewById(R.id.statusIndikator)
        val ivWeatherIcon: ImageView = view.findViewById(R.id.ivWeatherIcon)
        val tvNaziv: TextView = view.findViewById(R.id.tvNaziv)
        val tvDrzava: TextView = view.findViewById(R.id.tvDrzava)
        val tvOpis: TextView = view.findViewById(R.id.tvOpis)
        val tvTip: TextView = view.findViewById(R.id.tvTip)
        val tvTemperatura: TextView = view.findViewById(R.id.tvTemperatura)
        val tvMinMax: TextView = view.findViewById(R.id.tvMinMax)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lokacija, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lok = getItem(position)
        holder.tvNaziv.text = lok.naziv
        holder.tvDrzava.text = lok.drzava
        holder.tvTip.text = lok.tipPrikaza

        val p = prognozeMap[lok.naziv] ?: WeatherStaticData.getPrognozu(lok.naziv)

        val status = if (p != null) {
            when {
                p.vrijemeTipa == "stormy" -> "Oluja"
                p.temperatura > 35f -> "Vruće"
                p.vrijemeTipa == "rainy" -> "Padavine"
                p.vrijemeTipa == "snowy" || p.temperatura < 0f -> "Mraz"
                p.temperatura > 25f -> "Toplo"
                else -> "Vedro"
            }
        } else {
            WeatherStaticData.getStatus(lok.naziv)
        }

        holder.statusIndikator.contentDescription = status
        when (status) {
            "Vedro", "Toplo" -> holder.statusIndikator.setImageResource(R.drawable.ic_dot_green)
            "Padavine", "Mraz" -> holder.statusIndikator.setImageResource(R.drawable.ic_dot_blue)
            "Oluja", "Vruće" -> holder.statusIndikator.setImageResource(R.drawable.ic_dot_red)
            else -> holder.statusIndikator.setImageResource(R.drawable.ic_dot_green)
        }

        if (p == null) {
            holder.tvTemperatura.text = if (fahrenheit) "-- °F" else "-- °C"
            holder.tvOpis.text = ""
            holder.tvMinMax.text = ""
            holder.ivWeatherIcon.setImageResource(R.drawable.ic_weather_cloudy)
        } else {
            val temp = if (fahrenheit) (p.temperatura * 9f / 5f + 32f).toInt() else p.temperatura.toInt()
            holder.tvTemperatura.text = if (fahrenheit) "$temp°F" else "$temp°C"
            holder.tvOpis.text = p.opisVremena
            holder.tvMinMax.text = "${p.maxTemp.toInt()}/${p.minTemp.toInt()}"
            holder.ivWeatherIcon.setImageResource(ikonaZaTip(p.vrijemeTipa))
        }

        holder.itemView.setOnClickListener { onClick(lok) }
    }

    private fun ikonaZaTip(tip: String): Int = when (tip) {
        "sunny" -> R.drawable.ic_weather_sunny
        "partly_cloudy" -> R.drawable.ic_weather_partly_cloudy
        "rainy" -> R.drawable.ic_weather_rainy
        "snowy" -> R.drawable.ic_weather_snowy
        "stormy" -> R.drawable.ic_weather_stormy
        "foggy" -> R.drawable.ic_weather_foggy
        else -> R.drawable.ic_weather_cloudy
    }

    object DiffCallback : DiffUtil.ItemCallback<Lokacija>() {
        override fun areItemsTheSame(old: Lokacija, new: Lokacija) = old.naziv == new.naziv
        override fun areContentsTheSame(old: Lokacija, new: Lokacija) = old == new
    }
}
