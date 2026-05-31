package ba.etf.weatherwatch.ui

import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import ba.etf.weatherwatch.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val prefs = getSharedPreferences("ww_prefs", MODE_PRIVATE)

        val rbCelsius    = findViewById<RadioButton>(R.id.rbCelsius)
        val rbFahrenheit = findViewById<RadioButton>(R.id.rbFahrenheit)
        val rbTemaLight  = findViewById<RadioButton>(R.id.rbTemaLight)
        val rbTemaDark   = findViewById<RadioButton>(R.id.rbTemaDark)
        val rbTemaAuto   = findViewById<RadioButton>(R.id.rbTemaAuto)

        // Postavi početno stanje prema trenutnim prefs
        when (prefs.getString("jedinice", "celsius")) {
            "fahrenheit" -> rbFahrenheit.isChecked = true
            else         -> rbCelsius.isChecked = true
        }
        when (prefs.getString("tema", "auto")) {
            "light" -> rbTemaLight.isChecked = true
            "dark"  -> rbTemaDark.isChecked = true
            else    -> rbTemaAuto.isChecked = true
        }

        // Spremi u prefs pri kliku
        rbCelsius.setOnClickListener {
            prefs.edit().putString("jedinice", "celsius").apply()
        }
        rbFahrenheit.setOnClickListener {
            prefs.edit().putString("jedinice", "fahrenheit").apply()
        }
        rbTemaLight.setOnClickListener {
            prefs.edit().putString("tema", "light").apply()
        }
        rbTemaDark.setOnClickListener {
            prefs.edit().putString("tema", "dark").apply()
        }
        rbTemaAuto.setOnClickListener {
            prefs.edit().putString("tema", "auto").apply()
        }
    }
}