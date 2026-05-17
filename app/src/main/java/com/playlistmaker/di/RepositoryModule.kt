package com.playlistmaker.di

import com.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.playlistmaker.search.data.TracksRepositoryImpl
import com.playlistmaker.search.domain.SearchHistoryRepository
import com.playlistmaker.search.domain.TracksRepository
import com.playlistmaker.settings.data.ThemeRepositoryImpl
import com.playlistmaker.settings.domain.ThemeRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(
            sharedPreferences = get(),
            gson = get()
        )
    }

    single<ThemeRepository> {
        ThemeRepositoryImpl(get())
    }
}