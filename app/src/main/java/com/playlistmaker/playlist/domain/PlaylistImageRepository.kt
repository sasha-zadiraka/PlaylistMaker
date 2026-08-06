package com.playlistmaker.playlist.domain

import android.net.Uri

interface PlaylistImageRepository {

    suspend fun saveImage(uri: Uri): String
}