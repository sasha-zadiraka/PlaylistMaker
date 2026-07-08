package com.playlistmaker.search.data

import com.playlistmaker.search.data.mapper.toTrack
import com.playlistmaker.search.data.network.ItunesApi
import com.playlistmaker.search.domain.TracksRepository
import com.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val itunesApi: ItunesApi
) : TracksRepository {

    override fun searchTracks(query: String): Flow<List<Track>?> = flow {
        try {
            val response = itunesApi.search(query)
            val tracks = response.results.map { it.toTrack() }

            emit(tracks)
        } catch (e: Exception) {
            emit(null)
        }
    }
}