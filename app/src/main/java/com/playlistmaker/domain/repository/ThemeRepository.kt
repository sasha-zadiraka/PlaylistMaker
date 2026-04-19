package com.playlistmaker.domain.repository

interface ThemeRepository {
    fun isDarkThemeEnabled(): Boolean
    fun switchTheme(darkThemeEnabled: Boolean)
}