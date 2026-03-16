package com.playlistmaker.ui

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.playlistmaker.data.AppConstants.TRACK_KEY
import com.playlistmaker.model.Track
import com.playlistmaker.model.getCoverArtwork

class PlayerActivity : AppCompatActivity() {

    private lateinit var buttonBack: ImageButton
    private lateinit var coverArtwork: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var progressValue: TextView
    private lateinit var durationValue: TextView
    private lateinit var collectionValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var genreValue: TextView
    private lateinit var countryValue: TextView

    private lateinit var albumLabel: TextView
    private lateinit var yearLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initViews()

        buttonBack.setOnClickListener {
            finish()
        }

        (intent.getSerializableExtra(TRACK_KEY) as? Track)?.let {
            fillData(it)
        }
    }

    private fun initViews() {
        buttonBack = findViewById(R.id.buttonBack)
        coverArtwork = findViewById(R.id.coverArtwork)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        progressValue = findViewById(R.id.progressValue)
        durationValue = findViewById(R.id.durationValue)
        collectionValue = findViewById(R.id.collectionValue)
        yearValue = findViewById(R.id.yearValue)
        genreValue = findViewById(R.id.genreValue)
        countryValue = findViewById(R.id.countryValue)

        albumLabel = findViewById(R.id.albumLabel)
        yearLabel = findViewById(R.id.yearLabel)
    }

    private fun fillData(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        progressValue.text = "00:00"
        durationValue.text = track.trackTime
        genreValue.text = track.primaryGenreName
        countryValue.text = track.country

        albumLabel.isVisible = track.collectionName.isNotEmpty()
        collectionValue.isVisible = track.collectionName.isNotEmpty()
        collectionValue.text = track.collectionName

        yearLabel.isVisible = track.releaseDate.isNotEmpty()
        yearValue.isVisible = track.releaseDate.isNotEmpty()
        yearValue.text = track.releaseDate

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_cover_placeholder_233)
            .error(R.drawable.ic_cover_placeholder_233)
            .centerCrop()
            .into(coverArtwork)
    }
}