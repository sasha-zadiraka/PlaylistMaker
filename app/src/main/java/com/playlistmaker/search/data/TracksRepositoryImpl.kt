package com.playlistmaker.search.data

import com.playlistmaker.search.data.mapper.toTrack
import com.playlistmaker.search.data.network.ItunesApi
import com.playlistmaker.search.data.response.ItunesSearchResponse
import com.playlistmaker.search.domain.TracksRepository
import com.playlistmaker.search.domain.models.Track
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
                    val tracks = response.body()?.results?.map { it.toTrack() }.orEmpty()
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