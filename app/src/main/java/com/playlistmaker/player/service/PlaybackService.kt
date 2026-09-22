package com.playlistmaker.player.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.playlistmaker.R
import com.playlistmaker.util.AppConstants.PLAYER_PROGRESS_UPDATE_DELAY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlaybackService : Service(), PlaybackServiceContract {

    private val binder = PlaybackBinder()

    private var mediaPlayer: MediaPlayer? = null

    private var artistName: String = ""
    private var trackName: String = ""
    private var previewUrl: String = ""

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null

    private val stateFlow = MutableStateFlow(PlaybackState())

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent): IBinder {
        previewUrl = intent.getStringExtra(EXTRA_PREVIEW_URL).orEmpty()
        artistName = intent.getStringExtra(EXTRA_ARTIST_NAME).orEmpty()
        trackName = intent.getStringExtra(EXTRA_TRACK_NAME).orEmpty()

        preparePlayer()

        return binder
    }

    override fun onDestroy() {
        progressJob?.cancel()
        serviceScope.cancel()
        releasePlayer()
        super.onDestroy()
    }

    private fun preparePlayer() {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(previewUrl)

            setOnPreparedListener {
                stateFlow.update { PlaybackState(status = PlaybackStatus.PREPARED) }
            }

            setOnCompletionListener {
                progressJob?.cancel()
                stateFlow.update { PlaybackState(status = PlaybackStatus.COMPLETED) }
                hideNotification()
            }

            prepareAsync()
        }
    }

    private fun releasePlayer() {
        val player = mediaPlayer
        mediaPlayer = null
        player?.release()
    }

    override fun observeState(): StateFlow<PlaybackState> = stateFlow.asStateFlow()

    override fun play() {
        mediaPlayer?.start()

        stateFlow.update { it.copy(status = PlaybackStatus.PLAYING) }

        startProgressUpdates()
    }

    override fun pause() {
        mediaPlayer?.pause()
        progressJob?.cancel()

        stateFlow.update { it.copy(status = PlaybackStatus.PAUSED) }
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()

        progressJob = serviceScope.launch {
            while (isActive) {
                stateFlow.update {
                    it.copy(progress = mediaPlayer?.currentPosition ?: 0)
                }

                delay(PLAYER_PROGRESS_UPDATE_DELAY)
            }
        }
    }

    override fun showNotification() {
        if (stateFlow.value.status != PlaybackStatus.PLAYING) {
            return
        }

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            } else {
                0
            }
        )
    }

    override fun hideNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun buildNotification() =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.screen_main_title))
            .setContentText(
                getString(
                    R.string.playback_notification_track_info,
                    artistName,
                    trackName
                )
            )
            .setSmallIcon(R.drawable.ic_notification_24)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.playback_notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )

            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    inner class PlaybackBinder : Binder() {
        fun getService(): PlaybackServiceContract = this@PlaybackService
    }

    companion object {
        const val EXTRA_PREVIEW_URL = "extra_preview_url"
        const val EXTRA_ARTIST_NAME = "extra_artist_name"
        const val EXTRA_TRACK_NAME = "extra_track_name"

        private const val CHANNEL_ID = "playback_channel"
        private const val NOTIFICATION_ID = 100
    }
}
