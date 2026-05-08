package com.playlistmaker.settings.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.playlistmaker.creator.Creator

class SettingsViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(
            themeInteractor = Creator.provideThemeInteractor(context.applicationContext),
            sharingInteractor = Creator.provideSharingInteractor(context)
        ) as T
    }
}