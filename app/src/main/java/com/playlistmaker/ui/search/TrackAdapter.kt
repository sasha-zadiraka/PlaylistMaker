package com.playlistmaker.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.playlistmaker.model.Track

class TrackAdapter(
    private val tracks: List<Track>,
    private val onTrackClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position], onTrackClick)
    }

    override fun getItemCount(): Int = tracks.size
}
