package com.playlistmaker.di

import com.playlistmaker.medialibrary.ui.MediaLibraryViewModel
import com.playlistmaker.medialibrary.ui.favorites.FavoriteTracksViewModel
import com.playlistmaker.medialibrary.ui.playlists.PlaylistsViewModel
import com.playlistmaker.player.ui.PlayerViewModel
import com.playlistmaker.playlist.ui.CreatePlaylistViewModel
import com.playlistmaker.playlist.ui.EditPlaylistViewModel
import com.playlistmaker.playlist.ui.PlaylistInfoViewModel
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
            playerInteractor = get(),
            favoriteTracksInteractor = get(),
            playlistInteractor = get()
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
        FavoriteTracksViewModel(
            favoriteTracksInteractor = get()
        )
    }

    viewModel {
        PlaylistsViewModel(
            playlistInteractor = get()
        )
    }

    viewModel {
        CreatePlaylistViewModel(
            interactor = get()
        )
    }

    viewModel {
        EditPlaylistViewModel(
            interactor = get()
        )
    }

    viewModel {
        PlaylistInfoViewModel(
            playlistInteractor = get(),
            sharingInteractor = get()
        )
    }
}