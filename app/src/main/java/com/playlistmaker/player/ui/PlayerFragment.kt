package com.playlistmaker.player.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.playlistmaker.player.service.PlaybackService
import com.playlistmaker.playlist.domain.Playlist
import com.playlistmaker.search.domain.models.Track
import com.playlistmaker.search.domain.models.getCoverArtwork
import com.playlistmaker.util.AppConstants.TRACK_KEY
import com.playlistmaker.util.AppConstants.ZERO_TIME
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlayerViewModel>()

    private lateinit var bottomSheetBehavior:
            BottomSheetBehavior<LinearLayout>

    private var shouldOpenBottomSheet = false

    private var isServiceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as PlaybackService.PlaybackBinder
            viewModel.attachService(binder.getService())
        }

        override fun onServiceDisconnected(name: ComponentName) {
            viewModel.detachService()
        }
    }

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    private val playlistAdapter = PlaylistBottomSheetAdapter(
        onPlaylistClick = ::onPlaylistClicked
    )

    private val bottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(
                bottomSheet: View,
                newState: Int,
            ) {
                val currentBinding = _binding ?: return

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        currentBinding.overlay.isVisible = false
                        currentBinding.overlay.alpha = 0f
                    }

                    else -> {
                        currentBinding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(
                bottomSheet: View,
                slideOffset: Float,
            ) {
                val currentBinding = _binding ?: return

                if (slideOffset >= 0f) {
                    currentBinding.overlay.alpha =
                        slideOffset.coerceIn(0f, 1f)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlayerBinding.inflate(
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

        setupBottomSheet()
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        val track = arguments?.getParcelable<Track>(TRACK_KEY)

        track?.let {
            fillData(it)
            viewModel.setTrack(it)
            bindPlaybackService(it)
        }

        requestNotificationPermissionIfNeeded()
    }

    private fun bindPlaybackService(track: Track) {
        val intent = Intent(requireContext(), PlaybackService::class.java).apply {
            putExtra(PlaybackService.EXTRA_PREVIEW_URL, track.previewUrl)
            putExtra(PlaybackService.EXTRA_ARTIST_NAME, track.artistName)
            putExtra(PlaybackService.EXTRA_TRACK_NAME, track.trackName)
        }

        isServiceBound = requireContext().bindService(
            intent,
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        if (!hasNotificationPermission()) {
            requestNotificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }

        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupBottomSheet() {
        binding.overlay.isVisible = false
        binding.overlay.alpha = 0f

        bottomSheetBehavior = BottomSheetBehavior
            .from(binding.playlistsBottomSheet)
            .apply {
                isHideable = true
                skipCollapsed = true
                state = BottomSheetBehavior.STATE_HIDDEN
            }

        bottomSheetBehavior.addBottomSheetCallback(
            bottomSheetCallback
        )
    }

    private fun setupRecyclerView() {
        binding.playlistsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playlistAdapter
        }
    }

    private fun setupListeners() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonPlay.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.buttonFavorite.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.buttonAddToPlaylist.setOnClickListener {
            shouldOpenBottomSheet = true
            viewModel.loadPlaylists()
        }

        binding.overlay.setOnClickListener {
            closePlaylistsBottomSheet()
        }

        binding.newPlaylistButton.setOnClickListener {
            shouldOpenBottomSheet = false

            findNavController().navigate(
                R.id.action_playerFragment_to_createPlaylistFragment
            )
        }
    }

    private fun observeViewModel() {
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        viewModel.observePlaylists().observe(viewLifecycleOwner) { playlists ->
            playlistAdapter.setItems(playlists)
            binding.playlistsRecyclerView.requestLayout()

            if (shouldOpenBottomSheet) {
                shouldOpenBottomSheet = false

                binding.playlistsBottomSheet.post {
                    openPlaylistsBottomSheet()
                }
            }
        }

        viewModel.observeAddTrackResult().observe(viewLifecycleOwner) { result ->
            when (result) {
                is AddTrackToPlaylistResult.Added -> {
                    closePlaylistsBottomSheet()

                    Toast.makeText(
                        requireContext(),
                        getString(
                            R.string.screen_player_track_added_to_playlist,
                            result.playlistName
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is AddTrackToPlaylistResult.AlreadyAdded -> {
                    Toast.makeText(
                        requireContext(),
                        getString(
                            R.string.screen_player_track_already_added_to_playlist,
                            result.playlistName
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                AddTrackToPlaylistResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        R.string.screen_player_track_add_error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun openPlaylistsBottomSheet() {
        binding.overlay.isVisible = true
        binding.overlay.alpha = 1f

        bottomSheetBehavior.state =
            BottomSheetBehavior.STATE_EXPANDED
    }

    private fun closePlaylistsBottomSheet() {
        bottomSheetBehavior.state =
            BottomSheetBehavior.STATE_HIDDEN

        binding.overlay.isVisible = false
        binding.overlay.alpha = 0f
    }

    private fun onPlaylistClicked(playlist: Playlist) {
        viewModel.addTrackToPlaylist(playlist)
    }

    private fun fillData(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.progressValue.text = ZERO_TIME

        binding.durationValue.text = SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(track.trackTimeMillis)

        binding.genreValue.text = track.primaryGenreName
        binding.countryValue.text = track.country

        binding.albumLabel.isVisible =
            track.collectionName.isNotEmpty()

        binding.collectionValue.isVisible =
            track.collectionName.isNotEmpty()

        binding.collectionValue.text =
            track.collectionName

        binding.yearLabel.isVisible =
            track.releaseDate.isNotEmpty()

        binding.yearValue.isVisible =
            track.releaseDate.isNotEmpty()

        binding.yearValue.text =
            track.releaseDate

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_cover_placeholder_233)
            .error(R.drawable.ic_cover_placeholder_233)
            .centerCrop()
            .into(binding.coverArtwork)
    }

    private fun render(state: PlayerState) {
        binding.progressValue.text = state.progress

        binding.buttonPlay.isEnabled = state.isPrepared
        binding.buttonPlay.setPlaying(state.isPlaying)

        if (state.isFavorite) {
            binding.buttonFavorite.setImageResource(
                R.drawable.ic_favorite_active_23
            )
        } else {
            binding.buttonFavorite.setImageResource(
                R.drawable.ic_add_to_favorite_23
            )
        }
    }

    override fun onStart() {
        super.onStart()

        if (hasNotificationPermission()) {
            viewModel.onAppForegrounded()
        }
    }

    override fun onStop() {
        super.onStop()

        if (hasNotificationPermission()) {
            viewModel.onAppBackgrounded()
        }
    }

    override fun onDestroyView() {
        if (::bottomSheetBehavior.isInitialized) {
            bottomSheetBehavior.removeBottomSheetCallback(
                bottomSheetCallback
            )
        }

        if (isServiceBound) {
            requireContext().unbindService(serviceConnection)
            isServiceBound = false
        }

        viewModel.detachService()

        binding.playlistsRecyclerView.adapter = null
        _binding = null

        super.onDestroyView()
    }
}