package com.playlistmaker.data.response

import com.google.gson.annotations.SerializedName
import com.playlistmaker.data.dto.TrackDto

data class ItunesSearchResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<TrackDto>
)