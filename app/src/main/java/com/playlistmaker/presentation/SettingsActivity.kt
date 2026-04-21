package com.playlistmaker.presentation

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial
import com.playlistmaker.creator.Creator
import com.playlistmaker.domain.api.SharingInteractor
import com.playlistmaker.domain.api.ThemeInteractor

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var themeInteractor: ThemeInteractor
    private lateinit var sharingInteractor: SharingInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        themeInteractor = Creator.provideThemeInteractor(this)
        sharingInteractor = Creator.provideSharingInteractor(this)

        val buttonBack = findViewById<ImageButton>(R.id.button_back)
        val buttonShare = findViewById<FrameLayout>(R.id.button_share_app)
        val buttonSupport = findViewById<FrameLayout>(R.id.button_support)
        val buttonAgreement = findViewById<FrameLayout>(R.id.button_user_agreement)
        themeSwitcher = findViewById(R.id.themeSwitcher)

        themeSwitcher.isChecked = themeInteractor.isDarkThemeEnabled()

        buttonBack.setOnClickListener {
            finish()
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            themeInteractor.switchTheme(checked)
        }

        buttonShare.setOnClickListener {
            sharingInteractor.shareApp()
        }

        buttonSupport.setOnClickListener {
            sharingInteractor.openSupport()
        }

        buttonAgreement.setOnClickListener {
            sharingInteractor.openTerms()
        }
    }
}