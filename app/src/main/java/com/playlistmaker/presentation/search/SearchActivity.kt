package com.playlistmaker.presentation.search

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
import com.playlistmaker.creator.Creator
import com.playlistmaker.data.AppConstants.CLICK_DEBOUNCE_DELAY
import com.playlistmaker.data.AppConstants.KEY_SEARCH_TEXT
import com.playlistmaker.data.AppConstants.SEARCH_DEBOUNCE_DELAY
import com.playlistmaker.data.AppConstants.TRACK_KEY
import com.playlistmaker.domain.api.SearchHistoryInteractor
import com.playlistmaker.domain.api.TracksInteractor
import com.playlistmaker.domain.models.Track
import com.playlistmaker.presentation.player.PlayerActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var tracksInteractor: TracksInteractor
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor

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
    private val searchRunnable = Runnable { searchRequest() }

    private enum class SearchState {
        EMPTY,
        ERROR
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        initViews()
        initInteractors()
        initAdapters()
        setupListeners()
        showHistoryIfNeeded()

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

    private fun initInteractors() {
        tracksInteractor = Creator.provideTracksInteractor()
        searchHistoryInteractor = Creator.provideSearchHistoryInteractor(this)
    }

    private fun initAdapters() {
        trackAdapter = TrackAdapter(trackList) { track ->
            if (clickDebounce()) {
                searchHistoryInteractor.addTrack(track)
                openPlayer(track)
            }
        }

        historyAdapter = TrackAdapter(historyTrackList) { track ->
            if (clickDebounce()) {
                searchHistoryInteractor.addTrack(track)
                showHistoryIfNeeded()
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
            handler.removeCallbacks(searchRunnable)

            inputEditText.setText("")
            inputEditText.requestFocus()

            val imm = getSystemService(InputMethodManager::class.java)
            imm?.showSoftInput(inputEditText, InputMethodManager.SHOW_IMPLICIT)

            buttonClear.isVisible = false
            progressBar.isVisible = false

            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            recycler.isVisible = false

            showMessage(null, "")
            showHistoryIfNeeded()
        }

        buttonRetry.setOnClickListener {
            searchRequest()
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handler.removeCallbacks(searchRunnable)
                searchRequest()

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
                showHistoryIfNeeded()
            }
        }

        inputEditText.doOnTextChanged { text, _, _, _ ->
            searchText = text?.toString().orEmpty()
            buttonClear.isVisible = searchText.isNotEmpty()

            handler.removeCallbacks(searchRunnable)

            if (searchText.isEmpty()) {
                trackList.clear()
                trackAdapter.notifyDataSetChanged()
                recycler.isVisible = false
                progressBar.isVisible = false
                showMessage(null, "")
                showHistoryIfNeeded()
            } else {
                hideHistory()
                searchDebounce()
            }
        }

        buttonClearHistory.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            historyTrackList.clear()
            historyAdapter.notifyDataSetChanged()
            hideHistory()
        }
    }

    private fun searchRequest() {
        val query = inputEditText.text.toString().trim()
        if (query.isBlank()) return

        progressBar.isVisible = true
        recycler.isVisible = false
        hideHistory()
        showMessage(null, "")

        tracksInteractor.searchTracks(query) { tracks ->
            runOnUiThread {
                progressBar.isVisible = false

                when {
                    tracks == null -> {
                        showMessage(
                            SearchState.ERROR,
                            getString(R.string.screen_search_error_no_internet)
                        )
                    }

                    tracks.isEmpty() -> {
                        showMessage(
                            SearchState.EMPTY,
                            getString(R.string.screen_search_error_empty_response)
                        )
                    }

                    else -> {
                        trackList.clear()
                        trackList.addAll(tracks)
                        trackAdapter.notifyDataSetChanged()
                        recycler.isVisible = true
                        showMessage(null, "")
                    }
                }
            }
        }
    }

    private fun showHistoryIfNeeded() {
        val history = searchHistoryInteractor.getHistory()

        if (inputEditText.text.isEmpty() && history.isNotEmpty()) {
            historyTrackList.clear()
            historyTrackList.addAll(history)
            historyAdapter.notifyDataSetChanged()

            historyContainer.isVisible = true
            historyTitle.isVisible = true
            buttonClearHistory.isVisible = true

            recycler.isVisible = false
            placeholderMessage.isVisible = false
            buttonRetry.isVisible = false
            errorImage.isVisible = false
            progressBar.isVisible = false
        } else {
            hideHistory()
        }
    }

    private fun hideHistory() {
        historyContainer.isVisible = false
    }

    private fun showMessage(state: SearchState?, text: String) {
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
            SearchState.EMPTY -> {
                errorImage.setImageResource(R.drawable.ic_empty_track_list_120)
                buttonRetry.isVisible = false
            }

            SearchState.ERROR -> {
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

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}