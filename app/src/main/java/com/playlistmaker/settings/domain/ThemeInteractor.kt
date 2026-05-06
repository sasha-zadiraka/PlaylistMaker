package com.playlistmaker.settings.domain

interface ThemeInteractor {
    fun isDarkThemeEnabled(): Boolean
    fun switchTheme(darkThemeEnabled: Boolean)
}