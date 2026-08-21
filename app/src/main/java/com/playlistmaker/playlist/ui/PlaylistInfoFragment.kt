package com.playlistmaker.playlist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.playlistmaker.search.domain.models.Track
import com.playlistmaker.search.ui.TrackAdapter
import com.playlistmaker.util.AppConstants.TRACK_KEY
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistInfoFragment : Fragment() {

    private var _binding: FragmentPlaylistInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlaylistInfoViewModel>()

    private val trackAdapter = TrackAdapter(
        onTrackClick = { track -> openPlayer(track) },
        onTrackLongClick = { track -> showRemoveTrackDialog(track) }
    )

    private lateinit var menuBottomSheetBehavior:
            BottomSheetBehavior<LinearLayout>

    private var currentContent: PlaylistInfoState.Content? = null

    private var previousStatusBarColor: Int = 0

    private val menuBottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(
                bottomSheet: View,
                newState: Int
            ) {
                val currentBinding = _binding ?: return

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        currentBinding.menuOverlay.isVisible = false
                        currentBinding.menuOverlay.alpha = 0f
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        currentBinding.menuOverlay.isVisible = true
                        currentBinding.menuOverlay.alpha = 1f
                    }

                    else -> {
                        currentBinding.menuOverlay.isVisible = true
                    }
                }
            }

            override fun onSlide(
                bottomSheet: View,
                slideOffset: Float
            ) {
                val currentBinding = _binding ?: return

                if (slideOffset >= 0f) {
                    currentBinding.menuOverlay.alpha =
                        slideOffset.coerceIn(0f, 1f)
                }
            }
        }

    private val playlistId: Long
        get() = requireArguments().getLong("playlistId")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistInfoBinding.inflate(
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

        setupTracksList()
        setupMenuBottomSheet()
        setupListeners()
        observeState()

        viewModel.loadPlaylist(playlistId)
    }

    private fun alignShareButtonGapToSheet() {
        val currentBinding = _binding ?: return

        val baseMarginPx = (24 * resources.displayMetrics.density).toInt()
        val params = currentBinding.playlistName.layoutParams as ViewGroup.MarginLayoutParams

        if (params.topMargin != baseMarginPx) {
            params.topMargin = baseMarginPx
            currentBinding.playlistName.layoutParams = params
        }

        if (!currentBinding.playlistDescription.isVisible) {
            return
        }

        currentBinding.root.doOnPreDraw {
            val freshBinding = _binding ?: return@doOnPreDraw

            if (!freshBinding.playlistDescription.isVisible) {
                return@doOnPreDraw
            }

            val shareLocation = IntArray(2)
            freshBinding.shareButton.getLocationOnScreen(shareLocation)
            val shareBottom = shareLocation[1] + freshBinding.shareButton.height

            val sheetLocation = IntArray(2)
            freshBinding.tracksContainer.getLocationOnScreen(sheetLocation)
            val sheetTop = sheetLocation[1]

            val currentGapPx = sheetTop - shareBottom
            val freshParams =
                freshBinding.playlistName.layoutParams as ViewGroup.MarginLayoutParams

            val newMargin = (freshParams.topMargin + currentGapPx - baseMarginPx)
                .coerceAtLeast(0)

            if (freshParams.topMargin != newMargin) {
                freshParams.topMargin = newMargin
                freshBinding.playlistName.layoutParams = freshParams
            }
        }
    }

    private fun setupTracksList() {
        binding.tracksRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }
    }

    private fun setupMenuBottomSheet() {
        binding.menuOverlay.isVisible = false
        binding.menuOverlay.alpha = 0f

        menuBottomSheetBehavior = BottomSheetBehavior
            .from(binding.playlistMenuBottomSheet)
            .apply {
                isHideable = true
                skipCollapsed = true
                state = BottomSheetBehavior.STATE_HIDDEN
            }

        menuBottomSheetBehavior.addBottomSheetCallback(
            menuBottomSheetCallback
        )
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.shareButton.setOnClickListener {
            sharePlaylist()
        }

        binding.menuButton.setOnClickListener {
            openMenuBottomSheet()
        }

        binding.menuOverlay.setOnClickListener {
            closeMenuBottomSheet()
        }

        binding.menuShareButton.setOnClickListener {
            closeMenuBottomSheet()
            sharePlaylist()
        }

        binding.menuDeleteButton.setOnClickListener {
            closeMenuBottomSheet()
            showDeletePlaylistDialog()
        }

        binding.menuEditButton.setOnClickListener {
            closeMenuBottomSheet()
            openEditPlaylist()
        }
    }

    private fun openEditPlaylist() {
        findNavController().navigate(
            R.id.action_playlistInfoFragment_to_editPlaylistFragment,
            bundleOf("playlistId" to playlistId)
        )
    }

    private fun openMenuBottomSheet() {
        binding.menuOverlay.isVisible = true
        binding.menuOverlay.alpha = 0f

        menuBottomSheetBehavior.state =
            BottomSheetBehavior.STATE_EXPANDED
    }

    private fun closeMenuBottomSheet() {
        menuBottomSheetBehavior.state =
            BottomSheetBehavior.STATE_HIDDEN

        binding.menuOverlay.isVisible = false
        binding.menuOverlay.alpha = 0f
    }

    private fun sharePlaylist() {
        val content = currentContent ?: return

        if (content.playlist.trackCount == 0) {
            Toast.makeText(
                requireContext(),
                R.string.screen_playlist_share_empty,
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        viewModel.shareText(buildShareText(content))
    }

    private fun buildShareText(content: PlaylistInfoState.Content): String {
        val playlist = content.playlist
        val durationFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

        return buildString {
            append(playlist.name)

            if (playlist.description.isNotBlank()) {
                append('\n')
                append(playlist.description)
            }

            append('\n')
            append(
                getString(
                    R.string.screen_playlist_share_tracks_count,
                    playlist.trackCount
                )
            )

            content.tracks.forEachIndexed { index, track ->
                append('\n')
                append(index + 1)
                append(". ")
                append(track.artistName)
                append(" - ")
                append(track.trackName)
                append(" (")
                append(durationFormat.format(track.trackTimeMillis))
                append(')')
            }
        }
    }

    private fun showDeletePlaylistDialog() {
        val playlistName = currentContent?.playlist?.name ?: return

        MaterialAlertDialogBuilder(
            requireContext(),
            R.style.ThemeOverlay_PlaylistMaker_AlertDialog
        )
            .setMessage(
                getString(
                R.string.screen_playlist_delete_dialog_message,
                playlistName
                )
            )
            .setNegativeButton(
                R.string.screen_playlist_remove_track_dialog_negative
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(
                R.string.screen_playlist_remove_track_dialog_positive
            ) { dialog, _ ->
                dialog.dismiss()

                viewModel.deletePlaylist {
                    findNavController().popBackStack()
                }
            }
            .show()
    }

    private fun openPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_playlistInfoFragment_to_playerFragment,
            bundleOf(TRACK_KEY to track)
        )
    }

    private fun showRemoveTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(
            requireContext(),
            R.style.ThemeOverlay_PlaylistMaker_AlertDialog
        )
            .setMessage(R.string.screen_playlist_remove_track_dialog_message)
            .setNegativeButton(
                R.string.screen_playlist_remove_track_dialog_negative
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(
                R.string.screen_playlist_remove_track_dialog_positive
            ) { dialog, _ ->
                viewModel.removeTrackFromPlaylist(track)
                dialog.dismiss()
            }
            .show()
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

    private fun renderState(state: PlaylistInfoState) {
        when (state) {
            PlaylistInfoState.Loading -> Unit

            is PlaylistInfoState.Content -> {
                currentContent = state

                val playlist = state.playlist

                binding.playlistName.text = playlist.name

                binding.playlistDescription.isVisible =
                    playlist.description.isNotBlank()

                binding.playlistDescription.text =
                    playlist.description

                alignShareButtonGapToSheet()

                val durationText = resources.getQuantityString(
                    R.plurals.screen_playlist_duration_minutes,
                    state.totalDurationMinutes,
                    state.totalDurationMinutes
                )

                val trackCountText = resources.getQuantityString(
                    R.plurals.screen_playlist_tracks_count,
                    playlist.trackCount,
                    playlist.trackCount
                )

                binding.playlistDuration.text = getString(
                    R.string.screen_playlist_duration_and_tracks,
                    durationText,
                    trackCountText
                )

                trackAdapter.setItems(state.tracks)

                binding.tracksRecyclerView.isVisible =
                    state.tracks.isNotEmpty()

                binding.tracksEmptyContainer.isVisible =
                    state.tracks.isEmpty()

                binding.menuPlaylistName.text = playlist.name
                binding.menuPlaylistTrackCount.text = trackCountText

                if (playlist.coverPath.isBlank()) {
                    binding.playlistCoverPlaceholder.isVisible = true
                    binding.playlistCover.isVisible = false

                    Glide.with(binding.playlistCover)
                        .clear(binding.playlistCover)

                    Glide.with(binding.menuPlaylistCover)
                        .load(R.drawable.ic_cover_placeholder_103)
                        .centerCrop()
                        .into(binding.menuPlaylistCover)
                } else {
                    binding.playlistCoverPlaceholder.isVisible = false
                    binding.playlistCover.isVisible = true

                    Glide.with(binding.playlistCover)
                        .load(File(playlist.coverPath))
                        .error(R.drawable.ic_cover_placeholder_233)
                        .into(binding.playlistCover)

                    Glide.with(binding.menuPlaylistCover)
                        .load(File(playlist.coverPath))
                        .centerCrop()
                        .placeholder(R.drawable.ic_cover_placeholder_103)
                        .error(R.drawable.ic_cover_placeholder_103)
                        .into(binding.menuPlaylistCover)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val window = requireActivity().window
        previousStatusBarColor = window.statusBarColor
        window.statusBarColor = ContextCompat.getColor(
            requireContext(),
            R.color.yp_light_grey
        )
    }

    override fun onPause() {
        requireActivity().window.statusBarColor = previousStatusBarColor
        super.onPause()
    }

    override fun onDestroyView() {
        if (::menuBottomSheetBehavior.isInitialized) {
            menuBottomSheetBehavior.removeBottomSheetCallback(
                menuBottomSheetCallback
            )
        }

        _binding = null
        super.onDestroyView()
    }
}