package com.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.playlistmaker.search.domain.models.Track
import com.playlistmaker.search.domain.models.getCoverArtwork
import com.playlistmaker.util.AppConstants.TRACK_KEY
import com.playlistmaker.util.AppConstants.ZERO_TIME
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlayerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupListeners()

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        val track = arguments?.getSerializable(TRACK_KEY) as? Track

        track?.let {
            fillData(it)
            viewModel.preparePlayer(it.previewUrl)
        }
    }

    private fun setupListeners() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonPlay.setOnClickListener {
            viewModel.playbackControl()
        }
    }

    private fun fillData(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.progressValue.text = ZERO_TIME
        binding.durationValue.text = track.trackTime
        binding.genreValue.text = track.primaryGenreName
        binding.countryValue.text = track.country

        binding.albumLabel.isVisible = track.collectionName.isNotEmpty()
        binding.collectionValue.isVisible = track.collectionName.isNotEmpty()
        binding.collectionValue.text = track.collectionName

        binding.yearLabel.isVisible = track.releaseDate.isNotEmpty()
        binding.yearValue.isVisible = track.releaseDate.isNotEmpty()
        binding.yearValue.text = track.releaseDate

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_cover_placeholder_233)
            .error(R.drawable.ic_cover_placeholder_233)
            .centerCrop()
            .into(binding.coverArtwork)
    }

    private fun render(state: PlayerState) {
        binding.progressValue.text = state.progress

        if (state.isPlaying) {
            binding.buttonPlay.setImageResource(R.drawable.ic_pause_83)
        } else {
            binding.buttonPlay.setImageResource(R.drawable.ic_play_83)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}