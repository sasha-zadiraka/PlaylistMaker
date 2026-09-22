package com.playlistmaker.player.service

enum class PlaybackStatus {
    DEFAULT,
    PREPARED,
    PLAYING,
    PAUSED,
    COMPLETED
}

data class PlaybackState(
    val status: PlaybackStatus = PlaybackStatus.DEFAULT,
    val progress: Int = 0
)
