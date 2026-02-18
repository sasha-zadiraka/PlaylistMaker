package com.playlistmaker.ui.search

import android.annotation.SuppressLint
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
import com.playlistmaker.api.ItunesApi
import com.playlistmaker.data.AppConstants.ITUNES_BASE_URL
import com.playlistmaker.api.mapper.toTrack
import com.playlistmaker.api.response.ItunesSearchResponse
import com.playlistmaker.data.AppConstants.KEY_SEARCH_TEXT
import com.playlistmaker.model.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private var searchText: String = ""
    private lateinit var inputEditText: EditText
    private lateinit var buttonClear: ImageView
    private lateinit var recycler: RecyclerView
    private lateinit var placeholderMessage: TextView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var buttonRetry: Button
    private var lastSearchQuery: String? = null

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApi::class.java)

    private val trackList = mutableListOf<Track>()

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

        val buttonBack = findViewById<ImageButton>(R.id.button_back)
        inputEditText = findViewById(R.id.input_search)
        buttonClear = findViewById(R.id.clear_cross_icon)
        recycler = findViewById(R.id.tracksRecycler)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        buttonRetry = findViewById(R.id.buttonRetry)

        buttonBack.setOnClickListener {
            finish()
        }

        buttonRetry.setOnClickListener {
            lastSearchQuery?.let { search(it) }
        }

        buttonClear.setOnClickListener {
            inputEditText.setText("")
            inputEditText.clearFocus()

            val imm = getSystemService(android.view.inputmethod.InputMethodManager::class.java)
            imm?.hideSoftInputFromWindow(inputEditText.windowToken, 0)

            buttonClear.visibility = View.GONE

            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            recycler.visibility = View.GONE

            showMessage(null, "")
        }

        inputEditText.doOnTextChanged { text: CharSequence?, _: Int, _: Int, _: Int ->
            searchText = text?.toString().orEmpty()
            buttonClear.visibility = if (searchText.isEmpty()) View.GONE else View.VISIBLE
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

        trackAdapter = TrackAdapter(trackList)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = trackAdapter

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
                            showMessage(SearchState.EMPTY, getString(R.string.screen_search_error_empty_response))
                        }
                    } else {
                        recycler.visibility = View.GONE
                        showMessage(SearchState.ERROR, getString(R.string.screen_search_error_no_internet))
                    }
                }

                override fun onFailure(call: Call<ItunesSearchResponse>, t: Throwable) {
                    showMessage(SearchState.ERROR, getString(R.string.screen_search_error_no_internet))
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
}
