package com.playlistmaker.data.repository

import com.playlistmaker.data.mapper.toTrack
import com.playlistmaker.data.network.ItunesApi
import com.playlistmaker.data.response.ItunesSearchResponse
import com.playlistmaker.domain.models.Track
import com.playlistmaker.domain.repository.TracksRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TracksRepositoryImpl(
    private val itunesApi: ItunesApi
) : TracksRepository {

    override fun searchTracks(query: String, callback: (List<Track>?) -> Unit) {
        itunesApi.search(query).enqueue(object : Callback<ItunesSearchResponse> {

            override fun onResponse(
                call: Call<ItunesSearchResponse>,
                response: Response<ItunesSearchResponse>
            ) {
                if (response.isSuccessful) {
                    val tracks = response.body()?.results?.map { it.toTrack() }
                    callback(tracks)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ItunesSearchResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}