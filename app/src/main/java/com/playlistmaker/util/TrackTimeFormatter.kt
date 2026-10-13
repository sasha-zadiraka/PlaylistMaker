package com.playlistmaker.util

import java.text.SimpleDateFormat
import java.util.Locale

fun formatTrackTime(millis: Long): String {
    return SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
}
