package com.playlistmaker.playlist.data.db

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.playlistmaker.playlist.domain.Playlist

class PlaylistDbConverter(
    private val gson: Gson
) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            trackIds = gson.toJson(playlist.trackIds),
            trackCount = playlist.trackCount
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            id = playlistEntity.id,
            name = playlistEntity.name,
            description = playlistEntity.description,
            coverPath = playlistEntity.coverPath,
            trackIds = convertTrackIdsFromJson(playlistEntity.trackIds),
            trackCount = playlistEntity.trackCount
        )
    }

    private fun convertTrackIdsFromJson(trackIdsJson: String): List<Long> {
        if (trackIdsJson.isBlank()) {
            return emptyList()
        }

        val type = object : TypeToken<List<Long>>() {}.type

        return runCatching {
            gson.fromJson<List<Long>>(trackIdsJson, type)
        }.getOrDefault(emptyList())
    }
}