package com.playlistmaker.medialibrary.domain

import com.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {

    suspend fun addTrack(track: Track)

    suspend fun deleteTrack(track: Track)

    fun getFavoriteTracks(): Flow<List<Track>>
}