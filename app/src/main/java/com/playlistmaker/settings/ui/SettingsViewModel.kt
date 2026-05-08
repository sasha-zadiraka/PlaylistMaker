package com.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.playlistmaker.settings.domain.ThemeInteractor
import com.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val themeInteractor: ThemeInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<SettingsState>()

    fun observeState(): LiveData<SettingsState> = stateLiveData

    fun loadSettings() {
        stateLiveData.value = SettingsState(
            isDarkThemeEnabled = themeInteractor.isDarkThemeEnabled()
        )
    }

    fun switchTheme(isChecked: Boolean) {
        themeInteractor.switchTheme(isChecked)
        stateLiveData.value = SettingsState(isDarkThemeEnabled = isChecked)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }
}