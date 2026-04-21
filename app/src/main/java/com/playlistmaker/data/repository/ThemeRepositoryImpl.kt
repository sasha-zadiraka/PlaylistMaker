package com.playlistmaker.data.repository

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.playlistmaker.data.AppConstants.APP_DARK_THEME
import com.playlistmaker.domain.repository.ThemeRepository

class ThemeRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : ThemeRepository {

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(APP_DARK_THEME, false)
    }

    override fun switchTheme(darkThemeEnabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(APP_DARK_THEME, darkThemeEnabled)
        }

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}