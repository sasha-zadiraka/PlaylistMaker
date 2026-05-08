package com.playlistmaker.search.data.response

import com.google.gson.annotations.SerializedName
import com.playlistmaker.search.data.dto.TrackDto

data class ItunesSearchResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<TrackDto>
)