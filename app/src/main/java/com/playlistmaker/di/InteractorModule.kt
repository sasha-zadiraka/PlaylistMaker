package com.playlistmaker.di

import com.playlistmaker.player.domain.PlayerInteractor
import com.playlistmaker.player.domain.PlayerInteractorImpl
import com.playlistmaker.search.domain.SearchHistoryInteractor
import com.playlistmaker.search.domain.SearchHistoryInteractorImpl
import com.playlistmaker.search.domain.TracksInteractor
import com.playlistmaker.search.domain.TracksInteractorImpl
import com.playlistmaker.settings.domain.ThemeInteractor
import com.playlistmaker.settings.domain.ThemeInteractorImpl
import com.playlistmaker.sharing.domain.SharingInteractor
import com.playlistmaker.sharing.domain.SharingInteractorImpl
import com.playlistmaker.sharing.domain.models.EmailData
import com.playlistmaker.sharing.domain.models.SharingData
import com.example.playlistmaker.R
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val interactorModule = module {

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    single<ThemeInteractor> {
        ThemeInteractorImpl(get())
    }

    single<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }

    single<SharingData> {
        SharingData(
            shareAppLink = androidContext().getString(R.string.course_link),
            userAgreementLink = androidContext().getString(R.string.user_agreement_link)
        )
    }

    single<EmailData> {
        EmailData(
            email = androidContext().getString(R.string.user_email),
            subject = androidContext().getString(R.string.user_email_title),
            text = androidContext().getString(R.string.user_email_description)
        )
    }

    single<SharingInteractor> {
        SharingInteractorImpl(
            externalNavigator = get(),
            sharingData = get(),
            emailData = get()
        )
    }
}