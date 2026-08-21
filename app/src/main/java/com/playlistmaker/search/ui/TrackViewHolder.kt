package com.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.track_card, parent, false)
) {
    private val cover: ImageView = itemView.findViewById(R.id.trackCover)
    private val trackName: TextView = itemView.findViewById(R.id.trackTitle)
    private val artistName: TextView = itemView.findViewById(R.id.trackArtist)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)

    fun bind(
        model: Track,
        onTrackClick: (Track) -> Unit,
        onTrackLongClick: (Track) -> Unit = {}
    ) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(model.trackTimeMillis)

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_cover_placeholder_45)
            .error(R.drawable.ic_cover_placeholder_45)
            .centerCrop()
            .into(cover)

        itemView.setOnClickListener {
            onTrackClick(model)
        }

        itemView.setOnLongClickListener {
            onTrackLongClick(model)
            true
        }
    }
}