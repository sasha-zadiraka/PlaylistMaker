package com.playlistmaker.medialibrary.data

import com.playlistmaker.data.db.TrackDao
import com.playlistmaker.data.db.mapper.toEntity
import com.playlistmaker.data.db.mapper.toTrack
import com.playlistmaker.medialibrary.domain.FavoriteTracksRepository
import com.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val trackDao: TrackDao
) : FavoriteTracksRepository {

    override suspend fun addTrack(track: Track) {
        trackDao.insertTrack(track.toEntity())
    }

    override suspend fun deleteTrack(track: Track) {
        trackDao.deleteTrack(track.toEntity())
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return trackDao.getFavoriteTracks().map { entities ->
            entities.map { it.toTrack() }
        }
    }
}