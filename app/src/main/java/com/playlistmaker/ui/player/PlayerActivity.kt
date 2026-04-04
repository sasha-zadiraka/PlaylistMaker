package com.playlistmaker.ui.player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.playlistmaker.data.AppConstants.TRACK_KEY
import com.playlistmaker.data.AppConstants.ZERO_TIME
import com.playlistmaker.model.Track
import com.playlistmaker.model.getCoverArtwork
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var buttonBack: ImageButton
    private lateinit var buttonPlay: ImageButton
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

    private var mediaPlayer: MediaPlayer? = null

    private var playerState = PlayerState.DEFAULT

    private val handler = Handler(Looper.getMainLooper())

    private val progressRunnable = object : Runnable {
        override fun run() {
            if (playerState == PlayerState.PLAYING) {
                progressValue.text = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(mediaPlayer?.currentPosition ?: 0)
                handler.postDelayed(this, 300L)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)

        initViews()

        buttonBack.setOnClickListener {
            finish()
        }

        val track = intent.getSerializableExtra(TRACK_KEY) as? Track
        track?.let {
            fillData(it)
            preparePlayer(it.previewUrl)
        }

        buttonPlay.setOnClickListener {
            playbackControl()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById<ConstraintLayout>(R.id.playerRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        buttonBack = findViewById(R.id.buttonBack)
        buttonPlay = findViewById(R.id.buttonPlay)
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
        progressValue.text = ZERO_TIME
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

    private fun preparePlayer(url: String) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                playerState = PlayerState.PREPARED
                setPlayButtonIcon()
            }
            setOnCompletionListener {
                playerState = PlayerState.PREPARED
                progressValue.text = ZERO_TIME
                handler.removeCallbacks(progressRunnable)
                setPlayButtonIcon()
            }
        }
    }

    private fun playbackControl() {
        when (playerState) {
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            else -> pausePlayer()
        }
    }

    private fun startPlayer() {
        mediaPlayer?.start()
        playerState = PlayerState.PLAYING
        setPlayButtonIcon()
        handler.post(progressRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer?.pause()
        playerState = PlayerState.PAUSED
        setPlayButtonIcon()
        handler.removeCallbacks(progressRunnable)
    }

    private fun setPlayButtonIcon() {
        when (playerState) {
            PlayerState.PLAYING -> buttonPlay.setImageResource(R.drawable.ic_pause_83)
            else -> buttonPlay.setImageResource(R.drawable.ic_play_83)
        }
    }

    override fun onPause() {
        super.onPause()
        if (playerState == PlayerState.PLAYING) {
            pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(progressRunnable)
        mediaPlayer?.release()
        mediaPlayer = null
    }
}