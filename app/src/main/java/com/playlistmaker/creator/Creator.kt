package com.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.R
import com.google.gson.Gson
import com.playlistmaker.player.data.PlayerRepositoryImpl
import com.playlistmaker.player.domain.PlayerInteractor
import com.playlistmaker.player.domain.PlayerInteractorImpl
import com.playlistmaker.util.AppConstants.PREFS_NAME
import com.playlistmaker.search.data.network.NetworkClient
import com.playlistmaker.search.data.network.ItunesApi
import com.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.playlistmaker.settings.data.ThemeRepositoryImpl
import com.playlistmaker.search.data.TracksRepositoryImpl
import com.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.playlistmaker.search.domain.SearchHistoryInteractor
import com.playlistmaker.sharing.domain.SharingInteractor
import com.playlistmaker.settings.domain.ThemeInteractor
import com.playlistmaker.search.domain.TracksInteractor
import com.playlistmaker.search.domain.SearchHistoryInteractorImpl
import com.playlistmaker.sharing.domain.SharingInteractorImpl
import com.playlistmaker.settings.domain.ThemeInteractorImpl
import com.playlistmaker.search.domain.TracksInteractorImpl
import com.playlistmaker.sharing.domain.models.EmailData
import com.playlistmaker.sharing.domain.models.SharingData

object Creator {

    private fun provideItunesApi(): ItunesApi {
        return NetworkClient.retrofit.create(ItunesApi::class.java)
    }

    private fun provideTracksRepository(): TracksRepositoryImpl {
        return TracksRepositoryImpl(provideItunesApi())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(provideTracksRepository())
    }

    private fun provideSearchHistoryRepository(context: Context): SearchHistoryRepositoryImpl {
        return SearchHistoryRepositoryImpl(
            sharedPreferences = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE),
            gson = Gson()
        )
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(provideSearchHistoryRepository(context))
    }

    private fun provideThemeRepository(context: Context): ThemeRepositoryImpl {
        return ThemeRepositoryImpl(
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        )
    }

    fun provideThemeInteractor(context: Context): ThemeInteractor {
        return ThemeInteractorImpl(provideThemeRepository(context))
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        val externalNavigator = ExternalNavigatorImpl(context)

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
            externalNavigator = externalNavigator,
            sharingData = sharingData,
            emailData = emailData
        )
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(PlayerRepositoryImpl())
    }
}