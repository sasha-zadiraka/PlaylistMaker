package com.playlistmaker.settings.ui

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSwitcher: SwitchMaterial
    private val viewModel by viewModel<SettingsViewModel>()

    private lateinit var buttonBack: ImageButton
    private lateinit var buttonShare: FrameLayout
    private lateinit var buttonSupport: FrameLayout
    private lateinit var buttonAgreement: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initViews()
        setupListeners()

        viewModel.observeState().observe(this) { state ->
            render(state)
        }

        viewModel.loadSettings()
    }

    private fun initViews() {
        buttonBack = findViewById(R.id.button_back)
        buttonShare = findViewById(R.id.button_share_app)
        buttonSupport = findViewById(R.id.button_support)
        buttonAgreement = findViewById(R.id.button_user_agreement)
        themeSwitcher = findViewById(R.id.themeSwitcher)
    }

    private fun setupListeners() {
        buttonBack.setOnClickListener {
            finish()
        }

        buttonShare.setOnClickListener {
            viewModel.shareApp()
        }

        buttonSupport.setOnClickListener {
            viewModel.openSupport()
        }

        buttonAgreement.setOnClickListener {
            viewModel.openTerms()
        }
    }

    private fun render(state: SettingsState) {
        themeSwitcher.setOnCheckedChangeListener(null)

        if (themeSwitcher.isChecked != state.isDarkThemeEnabled) {
            themeSwitcher.isChecked = state.isDarkThemeEnabled
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
        }
    }
}