package com.playlistmaker.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.google.gson.Gson
import com.playlistmaker.api.ItunesApi
import com.playlistmaker.api.mapper.toTrack
import com.playlistmaker.api.response.ItunesSearchResponse
import com.playlistmaker.data.AppConstants.ITUNES_BASE_URL
import com.playlistmaker.data.AppConstants.KEY_SEARCH_TEXT
import com.playlistmaker.data.AppConstants.TRACK_KEY
import com.playlistmaker.model.Track
import com.playlistmaker.ui.PlayerActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var searchText: String = ""

    private lateinit var inputEditText: EditText
    private lateinit var buttonClear: ImageView
    private lateinit var buttonRetry: Button
    private lateinit var placeholderMessage: TextView
    private lateinit var historyTitle: TextView
    private lateinit var buttonClearHistory: Button
    private lateinit var historyContainer: LinearLayout

    private lateinit var recycler: RecyclerView
    private lateinit var historyRecycler: RecyclerView

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private lateinit var searchHistory: SearchHistory

    private val trackList = mutableListOf<Track>()
    private val historyTrackList = mutableListOf<Track>()

    private var lastSearchQuery: String? = null

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApi::class.java)

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
        inputEditText.setText(restoredText)
        inputEditText.setSelection(restoredText.length)
        buttonClear.visibility = if (restoredText.isEmpty()) View.GONE else View.VISIBLE
        searchText = restoredText
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        initViews()
        initSearchHistory()
        initAdapters()
        setupListeners()
        setupInsets()

        showHistoryIfNeeded()
    }

    private fun initViews() {
        inputEditText = findViewById(R.id.input_search)
        buttonClear = findViewById(R.id.clear_cross_icon)
        buttonRetry = findViewById(R.id.buttonRetry)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        historyTitle = findViewById(R.id.historyTitle)
        buttonClearHistory = findViewById(R.id.buttonClearHistory)
        historyContainer = findViewById(R.id.historyContainer)

        recycler = findViewById(R.id.tracksRecycler)
        historyRecycler = findViewById(R.id.tracksHistoryRecycler)
    }

    private fun initSearchHistory() {
        val sharedPrefs = getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs, Gson())
    }

    private fun initAdapters() {
        trackAdapter = TrackAdapter(trackList) { track ->
            searchHistory.addTrack(track)

            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra(TRACK_KEY, track)
            startActivity(intent)
        }

        historyAdapter = TrackAdapter(historyTrackList) { track ->
            searchHistory.addTrack(track)
            showHistoryIfNeeded()

            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra(TRACK_KEY, track)
            startActivity(intent)
        }

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = trackAdapter

        historyRecycler.layoutManager = LinearLayoutManager(this)
        historyRecycler.adapter = historyAdapter
    }

    private fun setupListeners() {
        val buttonBack = findViewById<ImageButton>(R.id.button_back)

        buttonBack.setOnClickListener {
            finish()
        }

        buttonClear.setOnClickListener {
            inputEditText.setText("")
            inputEditText.requestFocus()

            val imm = getSystemService(InputMethodManager::class.java)
            imm?.showSoftInput(inputEditText, InputMethodManager.SHOW_IMPLICIT)

            buttonClear.visibility = View.GONE

            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            recycler.visibility = View.GONE

            showMessage(null, "")
            showHistoryIfNeeded()
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                inputEditText.clearFocus()

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
            }
        }

        inputEditText.doOnTextChanged { text: CharSequence?, _, _, _ ->
            searchText = text?.toString().orEmpty()
            buttonClear.visibility = if (searchText.isEmpty()) View.GONE else View.VISIBLE

            if (searchText.isEmpty() && inputEditText.hasFocus()) {
                showHistoryIfNeeded()
            } else {
                hideHistory()
            }
        }

        buttonClearHistory.setOnClickListener {
            searchHistory.clearHistory()
            historyTrackList.clear()
            historyAdapter.notifyDataSetChanged()
            hideHistory()
        }
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById<LinearLayout>(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    enum class SearchState {
        EMPTY,
        ERROR
    }

    private fun search(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return

        lastSearchQuery = trimmed
        hideHistory()

        itunesService.search(trimmed)
            .enqueue(object : Callback<ItunesSearchResponse> {
                override fun onResponse(
                    call: Call<ItunesSearchResponse>,
                    response: Response<ItunesSearchResponse>
                ) {
                    val body = response.body()

                    if (response.isSuccessful && body != null) {
                        val tracks = body.results.map { it.toTrack() }

                        if (tracks.isNotEmpty()) {
                            trackList.clear()
                            trackList.addAll(tracks)
                            trackAdapter.notifyDataSetChanged()

                            recycler.visibility = View.VISIBLE
                            showMessage(null, "")
                        } else {
                            recycler.visibility = View.GONE
                            showMessage(
                                SearchState.EMPTY,
                                getString(R.string.screen_search_error_empty_response)
                            )
                        }
                    } else {
                        recycler.visibility = View.GONE
                        showMessage(
                            SearchState.ERROR,
                            getString(R.string.screen_search_error_no_internet)
                        )
                    }
                }

                override fun onFailure(call: Call<ItunesSearchResponse>, t: Throwable) {
                    recycler.visibility = View.GONE
                    showMessage(
                        SearchState.ERROR,
                        getString(R.string.screen_search_error_no_internet)
                    )
                }
            })
    }

    private fun search() {
        search(inputEditText.text.toString())
    }

    private fun showMessage(state: SearchState?, text: String) {
        val errorImage = findViewById<ImageView>(R.id.errorImage)

        if (state == null) {
            placeholderMessage.visibility = View.GONE
            errorImage.visibility = View.GONE
            buttonRetry.visibility = View.GONE
            return
        }

        placeholderMessage.visibility = View.VISIBLE
        placeholderMessage.text = text
        errorImage.visibility = View.VISIBLE

        when (state) {
            SearchState.EMPTY -> {
                errorImage.setImageResource(R.drawable.ic_empty_track_list_120)
                buttonRetry.visibility = View.GONE
            }

            SearchState.ERROR -> {
                errorImage.setImageResource(R.drawable.ic_no_internet_120)
                buttonRetry.visibility = View.VISIBLE
            }
        }

        trackList.clear()
        trackAdapter.notifyDataSetChanged()
    }

    private fun showHistoryIfNeeded() {
        val history = searchHistory.getHistory()

        if (inputEditText.text.isEmpty() && history.isNotEmpty()) {
            historyTrackList.clear()
            historyTrackList.addAll(history)
            historyAdapter.notifyDataSetChanged()

            historyContainer.visibility = View.VISIBLE
            historyTitle.visibility = View.VISIBLE
            buttonClearHistory.visibility = View.VISIBLE

            recycler.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
            buttonRetry.visibility = View.GONE
            findViewById<ImageView>(R.id.errorImage).visibility = View.GONE
        } else {
            hideHistory()
        }
    }

    private fun hideHistory() {
        historyContainer.visibility = View.GONE
    }
}