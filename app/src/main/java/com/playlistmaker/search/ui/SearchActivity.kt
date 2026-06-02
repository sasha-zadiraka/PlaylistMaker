package com.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.playlistmaker.player.ui.PlayerActivity
import com.playlistmaker.util.AppConstants.CLICK_DEBOUNCE_DELAY
import com.playlistmaker.util.AppConstants.KEY_SEARCH_TEXT
import com.playlistmaker.util.AppConstants.TRACK_KEY
import com.playlistmaker.search.domain.models.Track

class SearchActivity : AppCompatActivity() {
    private lateinit var buttonBack: ImageButton
    private lateinit var inputEditText: EditText
    private lateinit var buttonClear: ImageView
    private lateinit var buttonRetry: Button
    private lateinit var placeholderMessage: TextView
    private lateinit var historyTitle: TextView
    private lateinit var buttonClearHistory: Button
    private lateinit var historyContainer: LinearLayout
    private lateinit var errorImage: ImageView
    private lateinit var progressBar: ProgressBar

    private lateinit var recycler: RecyclerView
    private lateinit var historyRecycler: RecyclerView

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val trackList = mutableListOf<Track>()
    private val historyTrackList = mutableListOf<Track>()

    private var searchText: String = ""
    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    private val viewModel by viewModel<SearchViewModel>()

    private enum class SearchMessageState {
        EMPTY,
        ERROR
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        initViews()
        initAdapters()
        setupListeners()

        viewModel.observeState().observe(this) { state ->
            render(state)
        }

        viewModel.showHistoryIfNeeded()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById<LinearLayout>(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
        inputEditText.setText(restoredText)
        inputEditText.setSelection(restoredText.length)
        buttonClear.isVisible = restoredText.isNotEmpty()
        searchText = restoredText
        viewModel.onSearchTextChanged(restoredText)
    }

    private fun initViews() {
        buttonBack = findViewById(R.id.button_back)
        inputEditText = findViewById(R.id.input_search)
        buttonClear = findViewById(R.id.clear_cross_icon)
        buttonRetry = findViewById(R.id.buttonRetry)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        historyTitle = findViewById(R.id.historyTitle)
        buttonClearHistory = findViewById(R.id.buttonClearHistory)
        historyContainer = findViewById(R.id.historyContainer)
        errorImage = findViewById(R.id.errorImage)
        progressBar = findViewById(R.id.progressBar)
        recycler = findViewById(R.id.tracksRecycler)
        historyRecycler = findViewById(R.id.tracksHistoryRecycler)
    }

    private fun initAdapters() {
        trackAdapter = TrackAdapter(trackList) { track ->
            if (clickDebounce()) {
                viewModel.saveTrackToHistory(track)
                openPlayer(track)
            }
        }

        historyAdapter = TrackAdapter(historyTrackList) { track ->
            if (clickDebounce()) {
                viewModel.saveTrackToHistory(track)
                viewModel.showHistoryIfNeeded()
                openPlayer(track)
            }
        }

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = trackAdapter

        historyRecycler.layoutManager = LinearLayoutManager(this)
        historyRecycler.adapter = historyAdapter
    }

    private fun setupListeners() {
        buttonBack.setOnClickListener {
            finish()
        }

        buttonClear.setOnClickListener {
            inputEditText.setText("")
            inputEditText.requestFocus()

            val imm = getSystemService(InputMethodManager::class.java)
            imm?.showSoftInput(inputEditText, InputMethodManager.SHOW_IMPLICIT)

            buttonClear.isVisible = false
        }

        buttonRetry.setOnClickListener {
            viewModel.searchImmediately(inputEditText.text.toString())
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchImmediately(inputEditText.text.toString())

                val imm = getSystemService(InputMethodManager::class.java)
                imm?.hideSoftInputFromWindow(inputEditText.windowToken, 0)

                true
            } else {
                false
            }
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                hideHistory()
            } else if (inputEditText.text.isEmpty()) {
                viewModel.showHistoryIfNeeded()
            }
        }

        inputEditText.doOnTextChanged { text, _, _, _ ->
            searchText = text?.toString().orEmpty()
            buttonClear.isVisible = searchText.isNotEmpty()

            viewModel.onSearchTextChanged(searchText)
        }

        buttonClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    private fun hideHistory() {
        historyContainer.isVisible = false
    }

    private fun showMessage(state: SearchMessageState?, text: String) {
        if (state == null) {
            placeholderMessage.isVisible = false
            errorImage.isVisible = false
            buttonRetry.isVisible = false
            return
        }

        placeholderMessage.isVisible = true
        placeholderMessage.text = text
        errorImage.isVisible = true

        when (state) {
            SearchMessageState.EMPTY -> {
                errorImage.setImageResource(R.drawable.ic_empty_state_120)
                buttonRetry.isVisible = false
            }

            SearchMessageState.ERROR -> {
                errorImage.setImageResource(R.drawable.ic_no_internet_120)
                buttonRetry.isVisible = true
            }
        }

        recycler.isVisible = false
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(TRACK_KEY, track)
        startActivity(intent)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> {
                progressBar.isVisible = true
                recycler.isVisible = false
                historyContainer.isVisible = false
                placeholderMessage.isVisible = false
                errorImage.isVisible = false
                buttonRetry.isVisible = false
            }

            is SearchState.Content -> {
                progressBar.isVisible = false
                historyContainer.isVisible = false
                placeholderMessage.isVisible = false
                errorImage.isVisible = false
                buttonRetry.isVisible = false

                trackList.clear()
                trackList.addAll(state.tracks)
                trackAdapter.notifyDataSetChanged()

                recycler.isVisible = true
            }

            is SearchState.Empty -> {
                progressBar.isVisible = false
                recycler.isVisible = false
                historyContainer.isVisible = false

                showMessage(
                    SearchMessageState.EMPTY,
                    getString(R.string.screen_search_error_empty_response)
                )
            }

            is SearchState.Error -> {
                progressBar.isVisible = false
                recycler.isVisible = false
                historyContainer.isVisible = false

                showMessage(
                    SearchMessageState.ERROR,
                    getString(R.string.screen_search_error_no_internet)
                )
            }

            is SearchState.History -> {
                progressBar.isVisible = false
                recycler.isVisible = false
                placeholderMessage.isVisible = false
                errorImage.isVisible = false
                buttonRetry.isVisible = false

                historyTrackList.clear()
                historyTrackList.addAll(state.tracks)
                historyAdapter.notifyDataSetChanged()

                historyContainer.isVisible = state.tracks.isNotEmpty()
                historyTitle.isVisible = state.tracks.isNotEmpty()
                buttonClearHistory.isVisible = state.tracks.isNotEmpty()
            }

            is SearchState.NothingFound -> {
                progressBar.isVisible = false
                recycler.isVisible = false
                historyContainer.isVisible = false
                placeholderMessage.isVisible = false
                errorImage.isVisible = false
                buttonRetry.isVisible = false

                trackList.clear()
                trackAdapter.notifyDataSetChanged()
                historyTrackList.clear()
                historyAdapter.notifyDataSetChanged()
            }
        }
    }
}