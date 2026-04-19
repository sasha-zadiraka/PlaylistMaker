package com.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.R
import com.google.gson.Gson
import com.playlistmaker.data.AppConstants.PREFS_NAME
import com.playlistmaker.data.NetworkClient
import com.playlistmaker.data.network.ItunesApi
import com.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.playlistmaker.data.repository.ThemeRepositoryImpl
import com.playlistmaker.data.repository.TracksRepositoryImpl
import com.playlistmaker.data.sharing.ExternalNavigatorImpl
import com.playlistmaker.domain.api.SearchHistoryInteractor
import com.playlistmaker.domain.api.SharingInteractor
import com.playlistmaker.domain.api.ThemeInteractor
import com.playlistmaker.domain.api.TracksInteractor
import com.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.playlistmaker.domain.impl.SharingInteractorImpl
import com.playlistmaker.domain.impl.ThemeInteractorImpl
import com.playlistmaker.domain.impl.TracksInteractorImpl
import com.playlistmaker.domain.models.EmailData
import com.playlistmaker.domain.models.SharingData

object Creator {

    private fun provideItunesApi(): ItunesApi {
        return NetworkClient.retrofit.create(ItunesApi::class.java)
    }

    private fun provideTracksRepository() =
        TracksRepositoryImpl(provideItunesApi())

    private fun provideSearchHistoryRepository(context: Context) =
        SearchHistoryRepositoryImpl(
            sharedPreferences = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE),
            gson = Gson()
        )

    private fun provideThemeRepository(context: Context) =
        ThemeRepositoryImpl(
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        )

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(
            TracksRepositoryImpl(provideItunesApi())
        )
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(provideSearchHistoryRepository(context))
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
}