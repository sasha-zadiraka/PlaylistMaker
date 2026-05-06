package com.playlistmaker.player.ui

import com.playlistmaker.util.AppConstants.ZERO_TIME

data class PlayerState(
    val isPrepared: Boolean = false,
    val isPlaying: Boolean = false,
    val progress: String = ZERO_TIME
)