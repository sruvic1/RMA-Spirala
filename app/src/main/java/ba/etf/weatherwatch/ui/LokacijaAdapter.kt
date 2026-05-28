package ba.etf.weatherwatch.ui

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

class LokacijaAdapter(private val onClick: (Lokacija) -> Unit) :
    ListAdapter<Lokacija, LokacijaAdapter.ViewHolder>(DiffCallback) {

    var fahrenheit: Boolean = false

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lok = getItem(position)
        holder.tvNaziv.text = lok.naziv
        holder.tvDrzava.text = lok.drzava
        holder.tvTip.text = lok.tipPrikaza

        val status = WeatherStaticData.getStatus(lok.naziv)
        holder.statusIndikator.contentDescription = status

        // Postavljanje boje tačkice na osnovu statusa
        when (status) {
            "Vedro", "Toplo" -> holder.statusIndikator.setImageResource(R.drawable.ic_dot_green)
            "Padavine", "Mraz" -> holder.statusIndikator.setImageResource(R.drawable.ic_dot_blue)
            "Oluja", "Vruće" -> holder.statusIndikator.setImageResource(R.drawable.ic_dot_red)
            else -> holder.statusIndikator.setImageResource(R.drawable.ic_dot_green)
        }

        val p = WeatherStaticData.getPrognozu(lok.naziv)
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

            // Postavljanje ikone vremena
            when (p.vrijemeTipa) {
                "sunny" -> holder.ivWeatherIcon.setImageResource(R.drawable.ic_weather_sunny)
                "rainy" -> holder.ivWeatherIcon.setImageResource(R.drawable.ic_weather_rainy)
                "cloudy" -> holder.ivWeatherIcon.setImageResource(R.drawable.ic_weather_cloudy)
                else -> holder.ivWeatherIcon.setImageResource(R.drawable.ic_weather_cloudy)
            }
        }

        holder.itemView.setOnClickListener { onClick(lok) }
    }

    object DiffCallback : DiffUtil.ItemCallback<Lokacija>() {
        override fun areItemsTheSame(oldItem: Lokacija, newItem: Lokacija) = oldItem.naziv == newItem.naziv
        override fun areContentsTheSame(oldItem: Lokacija, newItem: Lokacija) = oldItem == newItem
    }
}