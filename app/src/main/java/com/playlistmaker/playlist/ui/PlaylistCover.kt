package com.playlistmaker.playlist.ui

import com.playlistmaker.playlist.domain.Playlist
import java.io.File

fun Playlist.coverFile(): File? {
    return coverPath.takeIf { it.isNotBlank() }?.let(::File)
}
