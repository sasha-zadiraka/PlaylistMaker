package com.playlistmaker.search.data.network

import com.playlistmaker.search.data.response.ItunesSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {
    @GET("search?entity=song")
    fun search(@Query("term") text: String): Call<ItunesSearchResponse>
}