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
import ba.etf.weatherwatch.model.SatnaPrognoza

class SatnaPrognozaAdapter :
    ListAdapter<SatnaPrognoza, SatnaPrognozaAdapter.ViewHolder>(DiffCallback) {

    var fahrenheit: Boolean = false

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSat: TextView = view.findViewById(R.id.tvSat)
        val ivSatnaIkona: ImageView = view.findViewById(R.id.ivSatnaIkona)
        val tvSatnaTemp: TextView = view.findViewById(R.id.tvSatnaTemp)
        val tvSatnaPadavine: TextView = view.findViewById(R.id.tvSatnaPadavine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_satna_prognoza, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.tvSat.text = item.sat
        val temp = if (fahrenheit) (item.temperatura * 9f / 5f + 32f).toInt() else item.temperatura.toInt()
        holder.tvSatnaTemp.text = if (fahrenheit) "$temp°F" else "$temp°C"
        holder.tvSatnaPadavine.text = "${item.padavinePostotak}%"
        holder.ivSatnaIkona.setImageResource(ikonaZaTip(item.vrijemeTipa))
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

    object DiffCallback : DiffUtil.ItemCallback<SatnaPrognoza>() {
        override fun areItemsTheSame(old: SatnaPrognoza, new: SatnaPrognoza) = old.sat == new.sat
        override fun areContentsTheSame(old: SatnaPrognoza, new: SatnaPrognoza) = old == new
    }
}
