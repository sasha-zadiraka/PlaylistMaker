package com.playlistmaker.domain.impl

import com.playlistmaker.domain.api.ThemeInteractor
import com.playlistmaker.domain.repository.ThemeRepository

class ThemeInteractorImpl(
    private val repository: ThemeRepository
) : ThemeInteractor {

    override fun isDarkThemeEnabled(): Boolean {
        return repository.isDarkThemeEnabled()
    }

    override fun switchTheme(darkThemeEnabled: Boolean) {
        repository.switchTheme(darkThemeEnabled)
    }
}