package com.playlistmaker.main.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.playlistmaker.ui.theme.PlaylistMakerTheme
import java.util.Locale

class MainActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val locale = Locale("ru")

        val configuration = Configuration(newBase.resources.configuration)
        configuration.setLocale(locale)

        super.attachBaseContext(
            newBase.createConfigurationContext(configuration)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            PlaylistMakerTheme {
                PlaylistMakerApp()
            }
        }
    }
}
