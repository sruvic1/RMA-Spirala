package ba.etf.weatherwatch.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ba.etf.weatherwatch.R
import ba.etf.weatherwatch.data.WeatherStaticData

class DetaljiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalji)

        val nazivLokacije = intent.getStringExtra("LOKACIJA") ?: return
        val prefs = getSharedPreferences("ww_prefs", MODE_PRIVATE)
        val fahrenheit = prefs.getString("jedinice", "celsius") == "fahrenheit"

        val p = WeatherStaticData.getPrognozu(nazivLokacije)
        val tvTemp = findViewById<TextView>(R.id.tvGlavnaTemp)

        if (p != null) {
            val temp = if (fahrenheit) (p.temperatura * 9f / 5f + 32f).toInt() else p.temperatura.toInt()
            tvTemp.text = if (fahrenheit) "$temp°F" else "$temp°C"
        }
    }
}