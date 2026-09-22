package com.playlistmaker.player.service

import kotlinx.coroutines.flow.StateFlow

interface PlaybackServiceContract {
    fun observeState(): StateFlow<PlaybackState>
    fun play()
    fun pause()
    fun showNotification()
    fun hideNotification()
}
