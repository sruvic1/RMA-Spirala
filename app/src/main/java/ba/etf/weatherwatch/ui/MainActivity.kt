package ba.etf.weatherwatch.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ba.etf.weatherwatch.R
import ba.etf.weatherwatch.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var lokacijaAdapter: LokacijaAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinnerFilter = findViewById<Spinner>(R.id.filterLokacija)
        val spinnerDrzave = findViewById<Spinner>(R.id.odabirDrzave)
        val spinnerGradovi = findViewById<Spinner>(R.id.odabirGrada)
        val spinnerTip = findViewById<Spinner>(R.id.odabirTipaPrikaza)
        val btnDodaj = findViewById<Button>(R.id.dodajLokacijuDugme)
        val btnPrognoza = findViewById<Button>(R.id.prikaziPrognozuDugme)
        val tvBroj = findViewById<TextView>(R.id.brojLokacija)
        val tvBrojKes = findViewById<TextView>(R.id.tvBrojKesiranih)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarMain)
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
        rv.adapter = lokacijaAdapter

        spinnerFilter.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, viewModel.filterOpcije)
        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                viewModel.postaviFilter(viewModel.filterOpcije[pos])
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        val drzaveLista = mutableListOf("Odaberi drzavu") + viewModel.sveDrzave.map { it.naziv }
        spinnerDrzave.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, drzaveLista)
        spinnerDrzave.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (pos == 0) viewModel.odaberiDrzavu(null) else viewModel.odaberiDrzavu(viewModel.sveDrzave[pos - 1])
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        viewModel.gradoviZaDrzavu.observe(this) { gradovi ->
            val gradoviLista = mutableListOf("Odaberi grad") + gradovi.map { it.naziv }
            spinnerGradovi.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, gradoviLista)
            spinnerGradovi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                    if (pos == 0) viewModel.odaberiGrad(null) else viewModel.odaberiGrad(gradovi[pos - 1])
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }

        val tipoviLista = mutableListOf("Odaberi tip") + viewModel.tipOpcije
        spinnerTip.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipoviLista)
        spinnerTip.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (pos == 0) viewModel.odaberiTip(null) else viewModel.odaberiTip(viewModel.tipOpcije[pos - 1])
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        viewModel.filterovaneLokacije.observe(this) { list ->
            lokacijaAdapter.submitList(list)
            tvBroj.text = "Pronađeno je ${list.size} lokacija"
        }

        viewModel.pronadjenePrognoze.observe(this) { progMap ->
            lokacijaAdapter.prognozeMap = progMap
        }

        viewModel.brojKesiranih.observe(this) { broj ->
            tvBrojKes.text = "Keširano: $broj prognoza"
        }

        viewModel.dugmeEnabled.observe(this) { btnDodaj.isEnabled = it }

        viewModel.isLoading.observe(this) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.greska.observe(this) { greska ->
            if (!greska.isNullOrEmpty()) {
                Toast.makeText(this, greska, Toast.LENGTH_LONG).show()
                viewModel.ocistiGresku()
            }
        }

        viewModel.uspjeh.observe(this) { poruka ->
            if (!poruka.isNullOrEmpty()) {
                Toast.makeText(this, poruka, Toast.LENGTH_SHORT).show()
                viewModel.ocistiUspjeh()
            }
        }

        btnDodaj.setOnClickListener {
            viewModel.dodajLokaciju()
            spinnerDrzave.setSelection(0)
            spinnerGradovi.setSelection(0)
            spinnerTip.setSelection(0)
        }

        btnPrognoza.setOnClickListener {
            val intent = Intent(this, PrognozaActivity::class.java).apply {
                putExtra("FILTER", viewModel.filterOpcije[spinnerFilter.selectedItemPosition])
            }
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        if (!::lokacijaAdapter.isInitialized) return
        val prefs = getSharedPreferences("ww_prefs", MODE_PRIVATE)
        val fahr = prefs.getString("jedinice", "celsius") == "fahrenheit"
        if (lokacijaAdapter.fahrenheit != fahr) {
            lokacijaAdapter.fahrenheit = fahr
            lokacijaAdapter.notifyDataSetChanged()
        }
        viewModel.osvjeziSveLokacije()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_refresh -> {
                viewModel.osvjeziSveLokacije()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
