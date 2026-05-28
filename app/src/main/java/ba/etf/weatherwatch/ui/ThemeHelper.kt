package ba.etf.weatherwatch.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

object ThemeHelper {
    fun apply(activity: AppCompatActivity) {
        val prefs = activity.getSharedPreferences("ww_prefs", Context.MODE_PRIVATE)
        AppCompatDelegate.setDefaultNightMode(
            when (prefs.getString("tema", "auto")) {
                "light" -> AppCompatDelegate.MODE_NIGHT_NO
                "dark" -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }
}