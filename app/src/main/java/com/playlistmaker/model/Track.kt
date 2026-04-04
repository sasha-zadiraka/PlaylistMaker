package com.playlistmaker.model

import java.io.Serializable

data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) : Serializable

fun Track.getCoverArtwork(): String {
    return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}