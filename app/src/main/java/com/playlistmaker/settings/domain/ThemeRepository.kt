package com.playlistmaker.settings.domain

interface ThemeRepository {
    fun isDarkThemeEnabled(): Boolean
    fun switchTheme(darkThemeEnabled: Boolean)
}