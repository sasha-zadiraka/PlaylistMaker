package com.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.playlistmaker.creator.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val themeInteractor = Creator.provideThemeInteractor(this)
        val darkTheme = themeInteractor.isDarkThemeEnabled()

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}