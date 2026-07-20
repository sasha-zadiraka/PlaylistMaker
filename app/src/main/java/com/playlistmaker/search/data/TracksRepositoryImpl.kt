package com.playlistmaker.search.data

import com.playlistmaker.data.db.TrackDao
import com.playlistmaker.search.data.mapper.toTrack
import com.playlistmaker.search.data.network.ItunesApi
import com.playlistmaker.search.domain.TracksRepository
import com.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val itunesApi: ItunesApi,
    private val trackDao: TrackDao
) : TracksRepository {

    override fun searchTracks(query: String): Flow<List<Track>?> = flow {
        try {
            val response = itunesApi.search(query)

            val favoriteIds = trackDao.getFavoriteTrackIds()

            val tracks = response.results.map { trackDto ->
                trackDto.toTrack().apply {
                    isFavorite = trackId in favoriteIds
                }
            }

            emit(tracks)
        } catch (e: Exception) {
            emit(null)
        }
    }
}