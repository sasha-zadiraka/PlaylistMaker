package com.playlistmaker.api.mapper

import com.playlistmaker.api.dto.TrackDto
import com.playlistmaker.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

fun TrackDto.toTrack(): Track {
    val formattedTrackLength = SimpleDateFormat("mm:ss", Locale.getDefault())
        .format(trackTimeMillis ?: 0L)

    val formattedReleaseYear = releaseDate?.take(4) ?: ""

    return Track(
        trackId = trackId ?: 0,
        trackName = trackName.orEmpty(),
        artistName = artistName.orEmpty(),
        trackTime = formattedTrackLength,
        artworkUrl100 = artworkUrl100.orEmpty(),
        collectionName = collectionName.orEmpty(),
        releaseDate = formattedReleaseYear,
        primaryGenreName = primaryGenreName.orEmpty(),
        country = country.orEmpty(),
        previewUrl = previewUrl.orEmpty()
    )
}