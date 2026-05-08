package com.playlistmaker.settings.ui

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var viewModel: SettingsViewModel

    private lateinit var buttonBack: ImageButton
    private lateinit var buttonShare: FrameLayout
    private lateinit var buttonSupport: FrameLayout
    private lateinit var buttonAgreement: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initViews()
        initViewModel()
        setupListeners()

        viewModel.loadSettings()
    }

    private fun initViews() {
        buttonBack = findViewById(R.id.button_back)
        buttonShare = findViewById(R.id.button_share_app)
        buttonSupport = findViewById(R.id.button_support)
        buttonAgreement = findViewById(R.id.button_user_agreement)
        themeSwitcher = findViewById(R.id.themeSwitcher)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(this)
        )[SettingsViewModel::class.java]

        viewModel.observeState().observe(this) { state ->
            render(state)
        }
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