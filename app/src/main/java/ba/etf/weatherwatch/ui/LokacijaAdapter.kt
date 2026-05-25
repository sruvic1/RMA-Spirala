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
import kotlin.math.roundToInt

class LokacijaAdapter(
    private val onKlikLokacija: (Lokacija) -> Unit
) : ListAdapter<Lokacija, LokacijaAdapter.ViewHolder>(LokacijaDiffCallback()) {

    var fahrenheit: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lokacija, parent, false)
        return ViewHolder(view, onKlikLokacija)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), fahrenheit)
    }

    class ViewHolder(
        itemView: View,
        private val onKlikLokacija: (Lokacija) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val statusIndikator: ImageView = itemView.findViewById(R.id.statusIndikator)
        private val ivWeatherIcon: ImageView = itemView.findViewById(R.id.ivWeatherIcon)
        private val tvNaziv: TextView = itemView.findViewById(R.id.tvNaziv)
        private val tvDrzava: TextView = itemView.findViewById(R.id.tvDrzava)
        private val tvOpis: TextView = itemView.findViewById(R.id.tvOpis)
        private val tvTip: TextView = itemView.findViewById(R.id.tvTip)
        private val tvTemperatura: TextView = itemView.findViewById(R.id.tvTemperatura)
        private val tvMinMax: TextView = itemView.findViewById(R.id.tvMinMax)

        @SuppressLint("SetTextI18n")
        fun bind(lokacija: Lokacija, fahrenheit: Boolean) {
            tvNaziv.text = lokacija.naziv as CharSequence?
            tvDrzava.text = lokacija.drzava as CharSequence?
            tvTip.text = lokacija.tipPrikaza as CharSequence?

            val status = WeatherStaticData.getStatus(lokacija.naziv as String as String)
            statusIndikator.contentDescription = status

            val dotRes = when (status) {
                "Vedro" -> R.drawable.ic_dot_green
                "Toplo" -> R.drawable.ic_dot_yellow
                "Vruće" -> R.drawable.ic_dot_orange
                "Padavine" -> R.drawable.ic_dot_blue
                "Mraz" -> R.drawable.ic_dot_blue
                "Oluja" -> R.drawable.ic_dot_red
                else -> R.drawable.ic_dot_green
            }
            statusIndikator.setImageResource(dotRes)

            val prognoza = WeatherStaticData.getPrognozu(lokacija.naziv as String)
            if (prognoza == null) {
                tvTemperatura.text = if (fahrenheit) "--°F" else "--°C"
                tvOpis.text = ""
                tvMinMax.text = ""
                ivWeatherIcon.setImageResource(R.drawable.ic_weather_cloudy)
            } else {
                tvOpis.text = prognoza.opisVremena

                if (fahrenheit) {
                    val tempF = ((prognoza.temperatura * 9f / 5f) + 32f).roundToInt()
                    val minF = ((((prognoza.minTemp.times(9f)) / 5f) + 32f)).roundToInt()
                    val maxF = ((((prognoza.maxTemp * 9f) / 5f) + 32f)).roundToInt()
                    tvTemperatura.text = "${tempF}°F"
                    tvMinMax.text = "$maxF/$minF"
                } else {
                    val tempC = prognoza.temperatura.toString()
                    val minC = prognoza.minTemp.toString()
                    val maxC = prognoza.maxTemp.toString()
                    tvTemperatura.text = "${tempC}°C"
                    tvMinMax.text = "$maxC/$minC"
                }

                val iconRes = when (prognoza.vrijemeTipa) {
                    "sunny" -> R.drawable.ic_weather_sunny
                    "partly_cloudy" -> R.drawable.ic_weather_partly_cloudy
                    "cloudy" -> R.drawable.ic_weather_cloudy
                    "rainy" -> R.drawable.ic_weather_rainy
                    "snowy" -> R.drawable.ic_weather_snowy
                    "stormy" -> R.drawable.ic_weather_stormy
                    "foggy" -> R.drawable.ic_weather_foggy
                    else -> R.drawable.ic_weather_cloudy
                }
                ivWeatherIcon.setImageResource(iconRes)
            }

            itemView.setOnClickListener { onKlikLokacija(lokacija) }
        }
    }

    class LokacijaDiffCallback : DiffUtil.ItemCallback<Lokacija>() {
        override fun areItemsTheSame(oldItem: Lokacija, newItem: Lokacija): Boolean {
            return oldItem.naziv == newItem.naziv && oldItem.drzava == newItem.drzava
        }
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Lokacija, newItem: Lokacija): Boolean {
            return oldItem == newItem
        }
    }
}