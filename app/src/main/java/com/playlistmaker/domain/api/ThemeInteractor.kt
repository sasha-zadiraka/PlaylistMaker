package com.playlistmaker.domain.api

interface ThemeInteractor {
    fun isDarkThemeEnabled(): Boolean
    fun switchTheme(darkThemeEnabled: Boolean)
}