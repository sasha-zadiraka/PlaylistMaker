package com.playlistmaker.di

import com.google.gson.Gson
import com.playlistmaker.medialibrary.data.FavoriteTracksRepositoryImpl
import com.playlistmaker.medialibrary.domain.FavoriteTracksRepository
import com.playlistmaker.playlist.data.db.PlaylistDbConverter
import com.playlistmaker.playlist.data.PlaylistImageRepositoryImpl
import com.playlistmaker.playlist.data.PlaylistRepositoryImpl
import com.playlistmaker.playlist.domain.PlaylistImageRepository
import com.playlistmaker.playlist.domain.PlaylistRepository
import com.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.playlistmaker.search.data.TracksRepositoryImpl
import com.playlistmaker.search.domain.SearchHistoryRepository
import com.playlistmaker.search.domain.TracksRepository
import com.playlistmaker.settings.data.ThemeRepositoryImpl
import com.playlistmaker.settings.domain.ThemeRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(
            itunesApi = get(),
            trackDao = get()
        )
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(
            sharedPreferences = get(),
            gson = get(),
            database = get()
        )
    }

    single<ThemeRepository> {
        ThemeRepositoryImpl(get())
    }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(
            trackDao = get()
        )
    }

    single {
        Gson()
    }

    single {
        PlaylistDbConverter(
            gson = get()
        )
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(
            playlistDao = get(),
            playlistTrackDao = get(),
            converter = get()
        )
    }

    single<PlaylistImageRepository> {
        PlaylistImageRepositoryImpl(
            context = androidContext()
        )
    }
}