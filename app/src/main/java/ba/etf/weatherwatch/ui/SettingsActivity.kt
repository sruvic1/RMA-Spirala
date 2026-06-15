package ba.etf.weatherwatch.ui

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ba.etf.weatherwatch.R
import ba.etf.weatherwatch.viewmodel.SettingsViewModel
import com.google.android.material.snackbar.Snackbar

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val prefs = getSharedPreferences("ww_prefs", MODE_PRIVATE)

        val rbCelsius = findViewById<RadioButton>(R.id.rbCelsius)
        val rbFahrenheit = findViewById<RadioButton>(R.id.rbFahrenheit)
        val rbTemaLight = findViewById<RadioButton>(R.id.rbTemaLight)
        val rbTemaDark = findViewById<RadioButton>(R.id.rbTemaDark)
        val rbTemaAuto = findViewById<RadioButton>(R.id.rbTemaAuto)
        val tvBrojKesiranih = findViewById<TextView>(R.id.tvBrojKesiranih)
        val btnObrisiKes = findViewById<Button>(R.id.btnObrisiKes)

        when (prefs.getString("jedinice", "celsius")) {
            "fahrenheit" -> rbFahrenheit.isChecked = true
            else -> rbCelsius.isChecked = true
        }
        when (prefs.getString("tema", "auto")) {
            "light" -> rbTemaLight.isChecked = true
            "dark" -> rbTemaDark.isChecked = true
            else -> rbTemaAuto.isChecked = true
        }

        rbCelsius.setOnClickListener { prefs.edit().putString("jedinice", "celsius").apply() }
        rbFahrenheit.setOnClickListener { prefs.edit().putString("jedinice", "fahrenheit").apply() }
        rbTemaLight.setOnClickListener { prefs.edit().putString("tema", "light").apply() }
        rbTemaDark.setOnClickListener { prefs.edit().putString("tema", "dark").apply() }
        rbTemaAuto.setOnClickListener { prefs.edit().putString("tema", "auto").apply() }

        viewModel.brojKesiranih.observe(this) { broj ->
            tvBrojKesiranih.text = "Keširano: $broj prognoza"
        }

        btnObrisiKes.setOnClickListener { view ->
            viewModel.obrisiKes()
            Snackbar.make(view, "Keš obrisan", Snackbar.LENGTH_SHORT).show()
        }
    }
}
