package com.playlistmaker.player.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.playlistmaker.creator.Creator

class PlayerViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayerViewModel(
            playerInteractor = Creator.providePlayerInteractor()
        ) as T
    }
}