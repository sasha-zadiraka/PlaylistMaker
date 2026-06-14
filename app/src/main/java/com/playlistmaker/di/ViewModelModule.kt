package com.playlistmaker.di

import com.playlistmaker.medialibrary.ui.MediaLibraryViewModel
import com.playlistmaker.medialibrary.ui.favorites.FavoriteTracksViewModel
import com.playlistmaker.medialibrary.ui.playlists.PlaylistsViewModel
import com.playlistmaker.player.ui.PlayerViewModel
import com.playlistmaker.search.ui.SearchViewModel
import com.playlistmaker.settings.ui.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(
            tracksInteractor = get(),
            searchHistoryInteractor = get()
        )
    }

    viewModel {
        PlayerViewModel(
            playerInteractor = get()
        )
    }

    viewModel {
        SettingsViewModel(
            themeInteractor = get(),
            sharingInteractor = get()
        )
    }

    viewModel {
        MediaLibraryViewModel()
    }

    viewModel {
        FavoriteTracksViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }
}