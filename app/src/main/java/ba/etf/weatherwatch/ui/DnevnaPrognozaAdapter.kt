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
import ba.etf.weatherwatch.model.DnevnaPrognoza

class DnevnaPrognozaAdapter :
    ListAdapter<DnevnaPrognoza, DnevnaPrognozaAdapter.ViewHolder>(DiffCallback) {

    var fahrenheit: Boolean = false

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDan: TextView = view.findViewById(R.id.tvDan)
        val ivDnevnaIkona: ImageView = view.findViewById(R.id.ivDnevnaIkona)
        val tvDnevnaMinMax: TextView = view.findViewById(R.id.tvDnevnaMinMax)
        val tvDnevnaPadavine: TextView = view.findViewById(R.id.tvDnevnaPadavine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dnevna_prognoza, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.tvDan.text = item.dan

        fun conv(t: Float) = if (fahrenheit) (t * 9f / 5f + 32f).toInt() else t.toInt()
        holder.tvDnevnaMinMax.text = "${conv(item.maxTemp)}°/${conv(item.minTemp)}°"
        holder.tvDnevnaPadavine.text = "${item.padavinePostotak}%"
        holder.ivDnevnaIkona.setImageResource(ikonaZaTip(item.vrijemeTipa))
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

    object DiffCallback : DiffUtil.ItemCallback<DnevnaPrognoza>() {
        override fun areItemsTheSame(old: DnevnaPrognoza, new: DnevnaPrognoza) = old.dan == new.dan
        override fun areContentsTheSame(old: DnevnaPrognoza, new: DnevnaPrognoza) = old == new
    }
}
