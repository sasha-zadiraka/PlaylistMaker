package com.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.R
import com.google.gson.Gson
import com.playlistmaker.player.data.PlayerRepositoryImpl
import com.playlistmaker.player.domain.PlayerInteractor
import com.playlistmaker.player.domain.PlayerInteractorImpl
import com.playlistmaker.player.domain.PlayerRepository
import com.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.playlistmaker.search.data.TracksRepositoryImpl
import com.playlistmaker.search.data.network.ItunesApi
import com.playlistmaker.search.data.network.NetworkClient
import com.playlistmaker.search.domain.SearchHistoryInteractor
import com.playlistmaker.search.domain.SearchHistoryInteractorImpl
import com.playlistmaker.search.domain.SearchHistoryRepository
import com.playlistmaker.search.domain.TracksInteractor
import com.playlistmaker.search.domain.TracksInteractorImpl
import com.playlistmaker.search.domain.TracksRepository
import com.playlistmaker.settings.data.ThemeRepositoryImpl
import com.playlistmaker.settings.domain.ThemeInteractor
import com.playlistmaker.settings.domain.ThemeInteractorImpl
import com.playlistmaker.settings.domain.ThemeRepository
import com.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.playlistmaker.sharing.domain.ExternalNavigator
import com.playlistmaker.sharing.domain.SharingInteractor
import com.playlistmaker.sharing.domain.SharingInteractorImpl
import com.playlistmaker.sharing.domain.models.EmailData
import com.playlistmaker.sharing.domain.models.SharingData
import com.playlistmaker.util.AppConstants.PREFS_NAME

object Creator {

    private fun provideItunesApi(): ItunesApi {
        return NetworkClient.retrofit.create(ItunesApi::class.java)
    }

    private fun provideTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(provideItunesApi())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(provideTracksRepository())
    }

    private fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            sharedPreferences = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE),
            gson = Gson()
        )
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(provideSearchHistoryRepository(context))
    }

    private fun provideThemeRepository(context: Context): ThemeRepository {
        return ThemeRepositoryImpl(
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        )
    }

    fun provideThemeInteractor(context: Context): ThemeInteractor {
        return ThemeInteractorImpl(provideThemeRepository(context))
    }

    private fun provideExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        val sharingData = SharingData(
            shareAppLink = context.getString(R.string.course_link),
            userAgreementLink = context.getString(R.string.user_agreement_link)
        )

        val emailData = EmailData(
            email = context.getString(R.string.user_email),
            subject = context.getString(R.string.user_email_title),
            text = context.getString(R.string.user_email_description)
        )

        return SharingInteractorImpl(
            externalNavigator = provideExternalNavigator(context),
            sharingData = sharingData,
            emailData = emailData
        )
    }

    private fun providePlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(providePlayerRepository())
    }
}