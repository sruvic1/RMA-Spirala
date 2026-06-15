package ba.etf.weatherwatch.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ba.etf.weatherwatch.R
import ba.etf.weatherwatch.model.Lokacija
import ba.etf.weatherwatch.model.Prognoza
import ba.etf.weatherwatch.viewmodel.DetaljiViewModel

class DetaljiActivity : AppCompatActivity() {

    private val viewModel: DetaljiViewModel by viewModels()
    private lateinit var satnaAdapter: SatnaPrognozaAdapter
    private lateinit var dnevnaAdapter: DnevnaPrognozaAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalji)

        val naziv = intent.getStringExtra("LOKACIJA") ?: run { finish(); return }
        val lat = intent.getDoubleExtra("LATITUDE", 0.0)
        val lon = intent.getDoubleExtra("LONGITUDE", 0.0)
        val tip = intent.getStringExtra("TIP") ?: "Po satu"
        val drzava = intent.getStringExtra("DRZAVA") ?: ""

        val prefs = getSharedPreferences("ww_prefs", MODE_PRIVATE)
        val fahrenheit = prefs.getString("jedinice", "celsius") == "fahrenheit"

        val progressBar = findViewById<ProgressBar>(R.id.progressBarDetalji)
        val tvTemp = findViewById<TextView>(R.id.tvGlavnaTemp)
        val tvOpis = findViewById<TextView>(R.id.tvGlavniOpis)
        val tvOsjecaj = findViewById<TextView>(R.id.tvOsjecaj)
        val tvMinMax = findViewById<TextView>(R.id.tvMinMaxGlavni)
        val tvVjetar = findViewById<TextView>(R.id.tvVjetar)
        val tvVlaznost = findViewById<TextView>(R.id.tvVlaznost)
        val tvUV = findViewById<TextView>(R.id.tvUV)
        val tvPritisak = findViewById<TextView>(R.id.tvPritisak)
        val tvVidljivost = findViewById<TextView>(R.id.tvVidljivost)
        val tvOblacnost = findViewById<TextView>(R.id.tvOblacnost)
        val ivIcon = findViewById<ImageView>(R.id.ivMainWeatherIcon)
        val ivBg = findViewById<ImageView>(R.id.ivBgGradient)
        val rvSatna = findViewById<RecyclerView>(R.id.rvSatnaPrognoza)
        val rvDnevna = findViewById<RecyclerView>(R.id.rvDnevnaPrognoza)

        satnaAdapter = SatnaPrognozaAdapter().apply { this.fahrenheit = fahrenheit }
        dnevnaAdapter = DnevnaPrognozaAdapter().apply { this.fahrenheit = fahrenheit }

        rvSatna.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvSatna.adapter = satnaAdapter

        rvDnevna.layoutManager = LinearLayoutManager(this)
        rvDnevna.adapter = dnevnaAdapter

        viewModel.isLoading.observe(this) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.greska.observe(this) { greska ->
            if (!greska.isNullOrEmpty()) {
                Toast.makeText(this, greska, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.prognoza.observe(this) { p ->
            if (p != null) populirajUI(p, fahrenheit, tvTemp, tvOpis, tvOsjecaj, tvMinMax,
                tvVjetar, tvVlaznost, tvUV, tvPritisak, tvVidljivost, tvOblacnost, ivIcon, ivBg)
        }

        val lokacija = Lokacija(naziv, drzava, lat, lon, tip, false)
        viewModel.ucitajPrognozu(lokacija)
    }

    @SuppressLint("SetTextI18n")
    private fun populirajUI(
        p: Prognoza, fahrenheit: Boolean,
        tvTemp: TextView, tvOpis: TextView, tvOsjecaj: TextView, tvMinMax: TextView,
        tvVjetar: TextView, tvVlaznost: TextView, tvUV: TextView,
        tvPritisak: TextView, tvVidljivost: TextView, tvOblacnost: TextView,
        ivIcon: ImageView, ivBg: ImageView
    ) {
        fun conv(t: Float) = if (fahrenheit) (t * 9f / 5f + 32f).toInt() else t.toInt()
        val unit = if (fahrenheit) "°F" else "°C"

        tvTemp.text = "${conv(p.temperatura)}$unit"
        tvOpis.text = p.opisVremena
        tvOsjecaj.text = "Osjeća se kao ${conv(p.osjecajTemperature)}$unit"
        tvMinMax.text = "${conv(p.maxTemp)}$unit / ${conv(p.minTemp)}$unit"
        tvVjetar.text = "💨 ${p.brzinaVjetra.toInt()} m/s ${p.smjerVjetra}"
        tvVlaznost.text = "💧 ${p.vlaznost}%"
        tvUV.text = "☀ UV ${p.uvIndeks.toInt()}"
        tvPritisak.text = "⬇ ${p.pritisak} hPa"
        tvVidljivost.text = "👁 ${p.vidljivost} km"
        tvOblacnost.text = "☁ ${p.oblacnost}%"

        ivIcon.setImageResource(ikonaZaTip(p.vrijemeTipa))
        ivBg.setBackgroundResource(pozadinaZaTip(p.vrijemeTipa))

        satnaAdapter.submitList(p.prognozaPoSatima)
        dnevnaAdapter.submitList(p.prognozaDani)
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

    private fun pozadinaZaTip(tip: String): Int = when (tip) {
        "sunny" -> R.drawable.bg_sunny
        "partly_cloudy" -> R.drawable.bg_partly_cloudy
        "rainy" -> R.drawable.bg_rainy
        "snowy" -> R.drawable.bg_snowy
        "stormy" -> R.drawable.bg_stormy
        "foggy" -> R.drawable.bg_foggy
        else -> R.drawable.bg_cloudy
    }
}
