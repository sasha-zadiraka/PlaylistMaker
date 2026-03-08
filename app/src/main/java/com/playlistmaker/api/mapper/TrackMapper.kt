package com.playlistmaker.api.mapper

import com.playlistmaker.api.dto.TrackDto
import com.playlistmaker.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

fun TrackDto.toTrack(): Track {
    val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault())
        .format(trackTimeMillis ?: 0L)

    return Track(
        trackId = trackId ?: 0,
        trackName = trackName.orEmpty(),
        artistName = artistName.orEmpty(),
        trackTime = formattedTime,
        artworkUrl100 = artworkUrl100.orEmpty()
    )
}