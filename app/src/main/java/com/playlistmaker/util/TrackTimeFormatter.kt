package com.playlistmaker.util

private const val MILLIS_IN_SECOND = 1000L
private const val SECONDS_IN_MINUTE = 60L

/**
 * Форматирует длительность в «mm:ss» без создания SimpleDateFormat на каждый вызов
 * (функция вызывается из composable для каждого элемента списка).
 */
fun formatTrackTime(millis: Long): String {
    val totalSeconds = millis / MILLIS_IN_SECOND
    val minutes = totalSeconds / SECONDS_IN_MINUTE
    val seconds = totalSeconds % SECONDS_IN_MINUTE

    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}
