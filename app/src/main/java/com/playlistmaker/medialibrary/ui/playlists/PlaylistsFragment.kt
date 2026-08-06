package com.playlistmaker.medialibrary.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null

    private val binding: FragmentPlaylistsBinding
        get() = requireNotNull(_binding)

    private val viewModel by viewModel<PlaylistsViewModel>()

    private val playlistAdapter = PlaylistAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeState()
    }

    private fun setupRecyclerView() {
        binding.playlistsRecyclerView.apply {
            layoutManager = GridLayoutManager(
                requireContext(),
                GRID_SPAN_COUNT
            )

            adapter = playlistAdapter

            addItemDecoration(
                PlaylistGridSpacingItemDecoration()
            )
        }
    }

    private fun setupListeners() {
        binding.newPlaylistButton.setOnClickListener {
            requireParentFragment()
                .findNavController()
                .navigate(
                    R.id.action_mediaLibraryFragment_to_createPlaylistFragment
                )
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.state.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun renderState(state: PlaylistsState) {
        when (state) {
            PlaylistsState.Empty -> {
                binding.emptyStateContainer.isVisible = true
                binding.playlistsRecyclerView.isVisible = false
            }

            is PlaylistsState.Content -> {
                binding.emptyStateContainer.isVisible = false
                binding.playlistsRecyclerView.isVisible = true

                playlistAdapter.setItems(state.playlists)
            }
        }
    }

    override fun onDestroyView() {
        binding.playlistsRecyclerView.adapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val GRID_SPAN_COUNT = 2

        fun newInstance(): PlaylistsFragment {
            return PlaylistsFragment()
        }
    }
}