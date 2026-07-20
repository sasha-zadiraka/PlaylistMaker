package com.playlistmaker.medialibrary.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.playlistmaker.search.domain.models.Track
import com.playlistmaker.search.ui.TrackAdapter
import com.playlistmaker.util.AppConstants.TRACK_KEY
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<FavoriteTracksViewModel>()

    private val trackAdapter by lazy {
        TrackAdapter { track ->
            openPlayer(track)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.favoriteTracksRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun render(state: FavoriteTracksState) {
        when (state) {
            FavoriteTracksState.Empty -> {
                binding.emptyPlaceholder.isVisible = true
                binding.favoriteTracksRecyclerView.isVisible = false
            }

            is FavoriteTracksState.Content -> {
                binding.emptyPlaceholder.isVisible = false
                binding.favoriteTracksRecyclerView.isVisible = true

                trackAdapter.setItems(state.tracks)
            }
        }
    }

    private fun openPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_mediaLibraryFragment_to_playerFragment,
            bundleOf(TRACK_KEY to track)
        )
    }

    override fun onDestroyView() {
        binding.favoriteTracksRecyclerView.adapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): FavoriteTracksFragment {
            return FavoriteTracksFragment()
        }
    }
}