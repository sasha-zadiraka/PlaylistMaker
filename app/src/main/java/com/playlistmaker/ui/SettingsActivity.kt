package com.playlistmaker.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial
import com.playlistmaker.App

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        val buttonBack = findViewById<ImageButton>(R.id.button_back)
        val buttonShareApp = findViewById<FrameLayout>(R.id.button_share_app)
        val buttonContactSupport = findViewById<FrameLayout>(R.id.button_support)
        val buttonUserAgreement = findViewById<FrameLayout>(R.id.button_user_agreement)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        buttonBack.setOnClickListener {
            finish()
        }

        val app = application as App
        themeSwitcher.isChecked = app.darkTheme
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        buttonShareApp.setOnClickListener{
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.course_link))
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.screen_settings_share_title)))
        }

        buttonContactSupport.setOnClickListener{
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:".toUri()
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.user_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.user_email_title))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.user_email_description))
            }

            startActivity(supportIntent)
        }

        buttonUserAgreement.setOnClickListener{
            val userAgreementIntent = Intent(Intent.ACTION_VIEW,
                getString(R.string.user_agreement_link).toUri())

            startActivity(userAgreementIntent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById<LinearLayout>(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}