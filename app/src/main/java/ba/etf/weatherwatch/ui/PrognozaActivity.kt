package ba.etf.weatherwatch.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ba.etf.weatherwatch.R
import ba.etf.weatherwatch.viewmodel.PrognozaViewModel

class PrognozaActivity : AppCompatActivity() {

    private val viewModel: PrognozaViewModel by viewModels()
    private lateinit var lokacijaAdapter: LokacijaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prognoza)

        val toolbar = findViewById<Toolbar>(R.id.toolbarPrognoza)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val filter = intent.getStringExtra("FILTER") ?: "Sve moje lokacije"
        supportActionBar?.title = filter

        val prefs = getSharedPreferences("ww_prefs", MODE_PRIVATE)
        val fahrenheit = prefs.getString("jedinice", "celsius") == "fahrenheit"

        val rv = findViewById<RecyclerView>(R.id.recyclerLokacije)
        rv.layoutManager = LinearLayoutManager(this)
        lokacijaAdapter = LokacijaAdapter { lok ->
            val intent = Intent(this, DetaljiActivity::class.java).apply {
                putExtra("LOKACIJA", lok.naziv)
                putExtra("LATITUDE", lok.latitude)
                putExtra("LONGITUDE", lok.longitude)
                putExtra("TIP", lok.tipPrikaza)
                putExtra("DRZAVA", lok.drzava)
            }
            startActivity(intent)
        }
        lokacijaAdapter.fahrenheit = fahrenheit
        rv.adapter = lokacijaAdapter

        viewModel.filtriraneLokacije.observe(this) { list ->
            lokacijaAdapter.submitList(list)
        }

        viewModel.prognozeMap.observe(this) { progMap ->
            lokacijaAdapter.prognozeMap = progMap
        }

        viewModel.ucitajSaFilterom(filter)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}