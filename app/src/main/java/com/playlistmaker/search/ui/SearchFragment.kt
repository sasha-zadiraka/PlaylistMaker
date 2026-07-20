package com.playlistmaker.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.playlistmaker.search.domain.models.Track
import com.playlistmaker.util.AppConstants.CLICK_DEBOUNCE_DELAY
import com.playlistmaker.util.AppConstants.KEY_SEARCH_TEXT
import com.playlistmaker.util.AppConstants.TRACK_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private var searchText: String = ""

    private val viewModel by viewModel<SearchViewModel>()

    private enum class SearchMessageState {
        EMPTY,
        ERROR
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        initClickDebounce()
        initAdapters()
        setupListeners()

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        savedInstanceState
            ?.getString(KEY_SEARCH_TEXT)
            ?.let { restoredText ->
                binding.inputSearch.setText(restoredText)
                binding.inputSearch.setSelection(restoredText.length)
                binding.clearCrossIcon.isVisible = restoredText.isNotEmpty()

                searchText = restoredText
                viewModel.onSearchTextChanged(restoredText)
            }

        viewModel.showHistoryIfNeeded()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_TEXT, searchText)
    }

    private fun initClickDebounce() {
        onTrackClickDebounce = debounce(
            delayMillis = CLICK_DEBOUNCE_DELAY,
            coroutineScope = lifecycleScope,
            useLastParam = false
        ) { track: Track ->
            viewModel.saveTrackToHistory(track)
            openPlayer(track)
        }
    }

    private fun initAdapters() {
        trackAdapter = TrackAdapter { track ->
            onTrackClickDebounce(track)
        }

        historyAdapter = TrackAdapter { track ->
            onTrackClickDebounce(track)
        }

        binding.tracksRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }

        binding.tracksHistoryRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun setupListeners() {
        binding.clearCrossIcon.setOnClickListener {
            binding.inputSearch.setText("")
            binding.inputSearch.requestFocus()

            val inputMethodManager =
                requireContext().getSystemService(InputMethodManager::class.java)

            inputMethodManager?.showSoftInput(
                binding.inputSearch,
                InputMethodManager.SHOW_IMPLICIT
            )

            binding.clearCrossIcon.isVisible = false
        }

        binding.buttonRetry.setOnClickListener {
            viewModel.searchImmediately(
                binding.inputSearch.text.toString()
            )
        }

        binding.inputSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchImmediately(
                    binding.inputSearch.text.toString()
                )

                val inputMethodManager =
                    requireContext().getSystemService(InputMethodManager::class.java)

                inputMethodManager?.hideSoftInputFromWindow(
                    binding.inputSearch.windowToken,
                    0
                )

                true
            } else {
                false
            }
        }

        binding.inputSearch.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                hideHistory()
            } else if (binding.inputSearch.text.isEmpty()) {
                viewModel.showHistoryIfNeeded()
            }
        }

        binding.inputSearch.doOnTextChanged { text, _, _, _ ->
            searchText = text?.toString().orEmpty()
            binding.clearCrossIcon.isVisible = searchText.isNotEmpty()

            viewModel.onSearchTextChanged(searchText)
        }

        binding.buttonClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    private fun hideHistory() {
        binding.historyContainer.isVisible = false
    }

    private fun showMessage(
        state: SearchMessageState?,
        text: String
    ) {
        if (state == null) {
            binding.placeholderMessage.isVisible = false
            binding.errorImage.isVisible = false
            binding.buttonRetry.isVisible = false
            return
        }

        binding.placeholderMessage.isVisible = true
        binding.placeholderMessage.text = text
        binding.errorImage.isVisible = true

        when (state) {
            SearchMessageState.EMPTY -> {
                binding.errorImage.setImageResource(
                    R.drawable.ic_empty_state_120
                )
                binding.buttonRetry.isVisible = false
            }

            SearchMessageState.ERROR -> {
                binding.errorImage.setImageResource(
                    R.drawable.ic_no_internet_120
                )
                binding.buttonRetry.isVisible = true
            }
        }

        binding.tracksRecycler.isVisible = false
    }

    private fun openPlayer(track: Track) {
        val bundle = Bundle().apply {
            putParcelable(TRACK_KEY, track)
        }

        findNavController().navigate(
            R.id.action_searchFragment_to_playerFragment,
            bundle
        )
    }

    private fun render(state: SearchState) {
        when (state) {
            SearchState.Loading -> {
                binding.progressBar.isVisible = true
                binding.tracksRecycler.isVisible = false
                binding.historyContainer.isVisible = false
                binding.placeholderMessage.isVisible = false
                binding.errorImage.isVisible = false
                binding.buttonRetry.isVisible = false
            }

            is SearchState.Content -> {
                binding.progressBar.isVisible = false
                binding.historyContainer.isVisible = false
                binding.placeholderMessage.isVisible = false
                binding.errorImage.isVisible = false
                binding.buttonRetry.isVisible = false

                trackAdapter.setItems(state.tracks)
                binding.tracksRecycler.isVisible = true
            }

            SearchState.Empty -> {
                binding.progressBar.isVisible = false
                binding.tracksRecycler.isVisible = false
                binding.historyContainer.isVisible = false

                showMessage(
                    SearchMessageState.EMPTY,
                    getString(
                        R.string.screen_search_error_empty_response
                    )
                )
            }

            SearchState.Error -> {
                binding.progressBar.isVisible = false
                binding.tracksRecycler.isVisible = false
                binding.historyContainer.isVisible = false

                showMessage(
                    SearchMessageState.ERROR,
                    getString(
                        R.string.screen_search_error_no_internet
                    )
                )
            }

            is SearchState.History -> {
                binding.progressBar.isVisible = false
                binding.tracksRecycler.isVisible = false
                binding.placeholderMessage.isVisible = false
                binding.errorImage.isVisible = false
                binding.buttonRetry.isVisible = false

                historyAdapter.setItems(state.tracks)

                val hasHistory = state.tracks.isNotEmpty()

                binding.historyContainer.isVisible = hasHistory
                binding.historyTitle.isVisible = hasHistory
                binding.buttonClearHistory.isVisible = hasHistory
            }

            SearchState.NothingFound -> {
                binding.progressBar.isVisible = false
                binding.tracksRecycler.isVisible = false
                binding.historyContainer.isVisible = false
                binding.placeholderMessage.isVisible = false
                binding.errorImage.isVisible = false
                binding.buttonRetry.isVisible = false

                trackAdapter.setItems(emptyList())
                historyAdapter.setItems(emptyList())
            }
        }
    }

    private fun <T> debounce(
        delayMillis: Long,
        coroutineScope: CoroutineScope,
        useLastParam: Boolean,
        action: (T) -> Unit
    ): (T) -> Unit {
        var debounceJob: Job? = null

        return { param ->
            if (useLastParam) {
                debounceJob?.cancel()

                debounceJob = coroutineScope.launch {
                    delay(delayMillis)
                    action(param)
                }
            } else {
                if (debounceJob?.isCompleted != false) {
                    debounceJob = coroutineScope.launch {
                        action(param)
                        delay(delayMillis)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.tracksRecycler.adapter = null
        binding.tracksHistoryRecycler.adapter = null
        _binding = null

        super.onDestroyView()
    }
}