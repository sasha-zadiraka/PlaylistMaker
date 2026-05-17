package com.playlistmaker.creator

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.playlistmaker.di.dataModule
import com.playlistmaker.di.interactorModule
import com.playlistmaker.di.repositoryModule
import com.playlistmaker.di.viewModelModule
import com.playlistmaker.settings.domain.ThemeInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)

            modules(
                dataModule,
                repositoryModule,
                interactorModule,
                viewModelModule
            )
        }

        val themeInteractor = getKoin().get<ThemeInteractor>()

        AppCompatDelegate.setDefaultNightMode(
            if (themeInteractor.isDarkThemeEnabled()) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}