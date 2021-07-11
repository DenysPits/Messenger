package com.example.messenger.view.activity

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class ThemeModeHandler(private val sharedPreferences: SharedPreferences) {
    private val key = "themeMode"

    fun restoreThemeMode() {
        val darkMode = sharedPreferences.getString(key, ThemeMode.SYSTEM.name)
        if (darkMode == ThemeMode.DARK.name) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else if (darkMode == ThemeMode.LIGHT.name) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun savePreference(themeMode: ThemeMode) {
        with(sharedPreferences.edit()) {
            putString(key, themeMode.name)
            apply()
        }
    }
}
