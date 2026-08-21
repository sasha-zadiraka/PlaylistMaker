package com.playlistmaker.search.data.mapper

import com.playlistmaker.search.data.dto.TrackDto
import com.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

fun TrackDto.toTrack(): Track {
    val formattedReleaseYear = releaseDate?.take(4) ?: ""

    return Track(
        trackId = trackId ?: 0L,
        trackName = trackName.orEmpty(),
        artistName = artistName.orEmpty(),
        trackTimeMillis = trackTimeMillis ?: 0L,
        artworkUrl100 = artworkUrl100.orEmpty(),
        collectionName = collectionName.orEmpty(),
        releaseDate = formattedReleaseYear,
        primaryGenreName = primaryGenreName.orEmpty(),
        country = country.orEmpty(),
        previewUrl = previewUrl.orEmpty()
    )
}