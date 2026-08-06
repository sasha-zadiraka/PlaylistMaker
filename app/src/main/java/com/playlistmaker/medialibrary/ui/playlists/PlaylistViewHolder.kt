package com.playlistmaker.medialibrary.ui.playlists

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import com.playlistmaker.playlist.domain.Playlist
import java.io.File

class PlaylistViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemPlaylistBinding.bind(itemView)

    fun bind(playlist: Playlist) {
        binding.playlistName.text = playlist.name

        binding.playlistTrackCount.text =
            itemView.context.resources.getQuantityString(
                R.plurals.screen_playlist_tracks_count,
                playlist.trackCount,
                playlist.trackCount
            )

        if (playlist.coverPath.isBlank()) {
            Glide.with(binding.playlistCover).clear(binding.playlistCover)

            binding.playlistCover.scaleType =
                android.widget.ImageView.ScaleType.CENTER

            binding.playlistCover.setImageResource(
                R.drawable.ic_cover_placeholder_103
            )
        } else {
            binding.playlistCover.scaleType =
                android.widget.ImageView.ScaleType.CENTER_INSIDE

            Glide.with(binding.playlistCover)
                .load(File(playlist.coverPath))
                .error(R.drawable.ic_cover_placeholder_103)
                .into(binding.playlistCover)
        }
    }
}