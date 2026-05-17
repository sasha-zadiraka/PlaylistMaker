package com.playlistmaker.di

import android.content.Context
import com.google.gson.Gson
import com.playlistmaker.player.data.PlayerRepositoryImpl
import com.playlistmaker.player.domain.PlayerRepository
import com.playlistmaker.search.data.network.ItunesApi
import com.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.playlistmaker.sharing.domain.ExternalNavigator
import com.playlistmaker.util.AppConstants.ITUNES_BASE_URL
import com.playlistmaker.util.AppConstants.PREFS_NAME
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single {
        Retrofit.Builder()
            .baseUrl(ITUNES_BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        OkHttpClient.Builder()
            .build()
    }

    single<ItunesApi> {
        get<Retrofit>().create(ItunesApi::class.java)
    }

    single {
        androidContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    factory {
        Gson()
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl()
    }
}