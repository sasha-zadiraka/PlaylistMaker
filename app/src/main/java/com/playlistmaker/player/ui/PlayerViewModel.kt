package com.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlistmaker.player.domain.PlayerInteractor
import com.playlistmaker.util.AppConstants.PLAYER_PROGRESS_UPDATE_DELAY
import com.playlistmaker.util.AppConstants.ZERO_TIME
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData(PlayerState())
    fun observeState(): LiveData<PlayerState> = stateLiveData

    private var progressJob: Job? = null

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
                stopProgressUpdate()
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
        stopProgressUpdate()

        stateLiveData.value = stateLiveData.value?.copy(
            isPlaying = false
        )
    }

    private fun startPlayer() {
        playerInteractor.start()

        stateLiveData.value = stateLiveData.value?.copy(
            isPlaying = true
        )

        startProgressUpdate()
    }

    private fun startProgressUpdate() {
        progressJob?.cancel()

        progressJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                stateLiveData.value = stateLiveData.value?.copy(
                    isPlaying = true,
                    progress = formatTime(playerInteractor.getCurrentPosition())
                )

                delay(PLAYER_PROGRESS_UPDATE_DELAY)
            }
        }
    }

    private fun stopProgressUpdate() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun formatTime(position: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(position)
    }

    override fun onCleared() {
        super.onCleared()
        stopProgressUpdate()
        playerInteractor.release()
    }
}