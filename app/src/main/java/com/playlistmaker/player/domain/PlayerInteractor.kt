package com.playlistmaker.player.domain

interface PlayerInteractor {
    fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun start()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
}