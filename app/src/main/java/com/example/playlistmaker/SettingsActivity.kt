package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val buttonBack = findViewById<ImageButton>(R.id.button_back)
        val buttonShareApp = findViewById<FrameLayout>(R.id.button_share_app)
        val buttonContactSupport = findViewById<FrameLayout>(R.id.button_support)
        val buttonUserAgreement = findViewById<FrameLayout>(R.id.button_user_agreement)

        buttonBack.setOnClickListener {
            finish()
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
    }
}