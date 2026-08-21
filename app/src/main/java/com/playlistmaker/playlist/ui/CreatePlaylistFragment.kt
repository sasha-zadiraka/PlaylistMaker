package com.playlistmaker.playlist.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

open class CreatePlaylistFragment :
    Fragment(R.layout.fragment_create_playlist) {

    private var _binding: FragmentCreatePlaylistBinding? = null

    protected val binding: FragmentCreatePlaylistBinding
        get() = requireNotNull(_binding)

    protected open val viewModel: CreatePlaylistViewModel by viewModel()

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri ?: return@registerForActivityResult

        viewModel.onCoverSelected(uri)
        showCover(uri)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentCreatePlaylistBinding.bind(view)

        restoreScreenData()
        setupListeners()
        observeState()
        registerBackPressedCallback()
    }

    private fun restoreScreenData() {
        binding.nameEditText.setText(viewModel.playlistName)

        binding.descriptionEditText.setText(
            viewModel.playlistDescription
        )

        viewModel.selectedCoverUri?.let { uri ->
            showCover(uri)
        }

        updateCreateButton()
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationIcon(
            R.drawable.ic_back_arrow_24
        )

        binding.toolbar.setNavigationOnClickListener {
            handleBackPressed()
        }

        binding.coverContainer.setOnClickListener {
            openPhotoPicker()
        }

        binding.nameEditText.doAfterTextChanged { editable ->
            viewModel.onNameChanged(
                editable?.toString().orEmpty()
            )

            updateCreateButton()
        }

        binding.descriptionEditText.doAfterTextChanged { editable ->
            viewModel.onDescriptionChanged(
                editable?.toString().orEmpty()
            )
        }

        binding.createButton.setOnClickListener {
            viewModel.createPlaylist()
        }
    }

    private fun openPhotoPicker() {
        imagePickerLauncher.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }

    protected fun showCover(uri: Uri) {
        binding.coverImage.isVisible = true
        binding.addCoverImage.isVisible = false

        Glide.with(this)
            .load(uri)
            .centerCrop()
            .into(binding.coverImage)
    }

    private fun updateCreateButton() {
        binding.createButton.isEnabled =
            viewModel.isCreateButtonEnabled
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

    protected open fun renderState(state: CreatePlaylistState) {
        when (state) {
            CreatePlaylistState.Editing -> {
                updateCreateButton()
            }

            CreatePlaylistState.Saving -> {
                binding.createButton.isEnabled = false
            }

            is CreatePlaylistState.Created -> {
                Toast.makeText(
                    requireContext(),
                    getString(
                        R.string.new_playlist_created_toast,
                        state.playlistName
                    ),
                    Toast.LENGTH_SHORT
                ).show()

                viewModel.resetState()
                findNavController().popBackStack()
            }

            is CreatePlaylistState.Error -> {
                Toast.makeText(
                    requireContext(),
                    getString(
                        R.string.new_playlist_creation_error
                    ),
                    Toast.LENGTH_SHORT
                ).show()

                viewModel.resetState()
                updateCreateButton()
            }
        }
    }

    private fun registerBackPressedCallback() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {

                    override fun handleOnBackPressed() {
                        handleBackPressed()
                    }
                }
            )
    }

    private fun handleBackPressed() {
        if (viewModel.hasUnsavedData) {
            showExitDialog()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun showExitDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(
                R.string.new_playlist_exit_dialog_title
            )
            .setMessage(
                R.string.new_playlist_exit_dialog_message
            )
            .setNegativeButton(
                R.string.new_playlist_exit_dialog_cancel
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(
                R.string.new_playlist_exit_dialog_finish
            ) { dialog, _ ->
                dialog.dismiss()
                findNavController().popBackStack()
            }
            .show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}