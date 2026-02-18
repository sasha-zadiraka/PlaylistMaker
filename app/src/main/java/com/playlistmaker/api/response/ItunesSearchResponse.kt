package com.playlistmaker.api.response

import com.google.gson.annotations.SerializedName
import com.playlistmaker.api.dto.TrackDto

data class ItunesSearchResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<TrackDto>
)