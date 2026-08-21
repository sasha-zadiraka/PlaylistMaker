package com.playlistmaker.playlist.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.playlistmaker.playlist.domain.Playlist
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class EditPlaylistFragment : CreatePlaylistFragment() {

    override val viewModel: EditPlaylistViewModel by viewModel()

    private val playlistId: Long
        get() = requireArguments().getLong(ARG_PLAYLIST_ID)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.title =
            getString(R.string.edit_playlist_screen_title)

        binding.createButton.text =
            getString(R.string.edit_playlist_save_button)

        viewModel.init(playlistId)
        observeEditingPlaylist()
    }

    private fun observeEditingPlaylist() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.editingPlaylist.collect { playlist ->
                    playlist ?: return@collect
                    fillPlaylistData(playlist)
                }
            }
        }
    }

    private fun fillPlaylistData(playlist: Playlist) {
        binding.nameEditText.setText(playlist.name)
        binding.descriptionEditText.setText(playlist.description)

        if (playlist.coverPath.isNotBlank()) {
            showCover(coverUriFromPath(playlist.coverPath))
        }
    }

    private fun coverUriFromPath(path: String): Uri {
        return File(path).toUri()
    }

    override fun renderState(state: CreatePlaylistState) {
        if (state is CreatePlaylistState.Created) {
            viewModel.resetState()
            findNavController().popBackStack()
            return
        }

        super.renderState(state)
    }

    private companion object {
        private const val ARG_PLAYLIST_ID = "playlistId"
    }
}
