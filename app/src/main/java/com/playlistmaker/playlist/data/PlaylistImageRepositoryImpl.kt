package com.playlistmaker.playlist.data

import android.content.Context
import android.net.Uri
import com.playlistmaker.playlist.domain.PlaylistImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class PlaylistImageRepositoryImpl(
    private val context: Context
) : PlaylistImageRepository {

    override suspend fun saveImage(uri: Uri): String =
        withContext(Dispatchers.IO) {
            val coversDirectory = File(
                context.filesDir,
                PLAYLIST_COVERS_DIRECTORY
            )

            if (!coversDirectory.exists()) {
                coversDirectory.mkdirs()
            }

            val coverFile = File(
                coversDirectory,
                "${UUID.randomUUID()}.jpg"
            )

            context.contentResolver
                .openInputStream(uri)
                ?.use { inputStream ->
                    coverFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                ?: throw IllegalStateException(
                    "Unable to open selected image"
                )

            coverFile.absolutePath
        }

    companion object {
        private const val PLAYLIST_COVERS_DIRECTORY =
            "playlist_covers"
    }
}