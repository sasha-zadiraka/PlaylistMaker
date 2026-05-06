package com.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.playlistmaker.medialibrary.ui.MediaLibraryActivity
import com.playlistmaker.search.ui.SearchActivity
import com.playlistmaker.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<Button>(R.id.button_search)
        val buttonMediaLibrary = findViewById<Button>(R.id.button_media_library)
        val buttonSettings = findViewById<Button>(R.id.button_settings)

        buttonSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        buttonMediaLibrary.setOnClickListener {
            startActivity(Intent(this, MediaLibraryActivity::class.java))
        }

        buttonSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}