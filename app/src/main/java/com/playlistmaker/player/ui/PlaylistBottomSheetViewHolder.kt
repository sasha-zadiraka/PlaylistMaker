package com.playlistmaker.player.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBottomSheetBinding
import com.playlistmaker.playlist.domain.Playlist
import java.io.File

class PlaylistBottomSheetViewHolder(
    itemView: View,
    private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val binding =
        ItemPlaylistBottomSheetBinding.bind(itemView)

    fun bind(playlist: Playlist) {
        binding.playlistName.text = playlist.name

        binding.playlistTrackCount.text =
            itemView.context.resources.getQuantityString(
                R.plurals.screen_playlist_tracks_count,
                playlist.trackCount,
                playlist.trackCount
            )

        if (playlist.coverPath.isBlank()) {
            Glide.with(binding.playlistCover)
                .load(R.drawable.ic_cover_placeholder_103)
                .centerCrop()
                .into(binding.playlistCover)
        } else {
            Glide.with(binding.playlistCover)
                .load(File(playlist.coverPath))
                .centerCrop()
                .placeholder(R.drawable.ic_cover_placeholder_103)
                .error(R.drawable.ic_cover_placeholder_103)
                .into(binding.playlistCover)
        }

        itemView.setOnClickListener {
            onPlaylistClick(playlist)
        }
    }
}