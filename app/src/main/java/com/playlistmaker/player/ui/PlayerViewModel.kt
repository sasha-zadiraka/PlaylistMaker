package com.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.playlistmaker.player.domain.PlayerInteractor
import com.playlistmaker.util.AppConstants.PLAYER_PROGRESS_UPDATE_DELAY
import com.playlistmaker.util.AppConstants.ZERO_TIME
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData(PlayerState())
    fun observeState(): LiveData<PlayerState> = stateLiveData

    private val handler = Handler(Looper.getMainLooper())

    private val progressRunnable = object : Runnable {
        override fun run() {
            if (playerInteractor.isPlaying()) {
                stateLiveData.postValue(
                    stateLiveData.value?.copy(
                        isPlaying = true,
                        progress = formatTime(playerInteractor.getCurrentPosition())
                    )
                )
                handler.postDelayed(this, PLAYER_PROGRESS_UPDATE_DELAY)
            }
        }
    }

    fun preparePlayer(url: String) {
        playerInteractor.prepare(
            url = url,
            onPrepared = {
                stateLiveData.postValue(
                    stateLiveData.value?.copy(
                        isPrepared = true,
                        isPlaying = false
                    )
                )
            },
            onCompletion = {
                handler.removeCallbacks(progressRunnable)
                stateLiveData.postValue(
                    stateLiveData.value?.copy(
                        isPrepared = true,
                        isPlaying = false,
                        progress = ZERO_TIME
                    )
                )
            }
        )
    }

    fun playbackControl() {
        val currentState = stateLiveData.value ?: PlayerState()

        if (!currentState.isPrepared) return

        if (currentState.isPlaying) {
            pausePlayer()
        } else {
            startPlayer()
        }
    }

    fun pausePlayer() {
        playerInteractor.pause()
        handler.removeCallbacks(progressRunnable)

        stateLiveData.value = stateLiveData.value?.copy(
            isPlaying = false
        )
    }

    private fun startPlayer() {
        playerInteractor.start()

        stateLiveData.value = stateLiveData.value?.copy(
            isPlaying = true
        )

        handler.post(progressRunnable)
    }

    private fun formatTime(position: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(position)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(progressRunnable)
        playerInteractor.release()
    }
}