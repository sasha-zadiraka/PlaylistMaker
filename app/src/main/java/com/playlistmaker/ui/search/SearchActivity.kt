package com.playlistmaker.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.playlistmaker.model.Track

class SearchActivity : AppCompatActivity() {
    private var searchText: String = ""
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recycler: RecyclerView

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
        inputEditText.setText(restoredText)
        inputEditText.setSelection(restoredText.length)
        clearButton.visibility = if (restoredText.isEmpty()) View.GONE else View.VISIBLE
        searchText = restoredText
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        val buttonBack = findViewById<ImageButton>(R.id.button_back)
        inputEditText = findViewById(R.id.input_search)
        clearButton = findViewById(R.id.clear_cross_icon)
        recycler = findViewById(R.id.tracksRecycler)

        val tracks = createMockedTracks()

        buttonBack.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            inputEditText.clearFocus()

            val imm = getSystemService(android.view.inputmethod.InputMethodManager::class.java)
            imm?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        inputEditText.doOnTextChanged {
                text: CharSequence?, _: Int, _: Int, _: Int ->
            searchText = text?.toString().orEmpty()
            clearButton.visibility = if (searchText.isEmpty()) View.GONE else View.VISIBLE
        }

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = TrackAdapter(tracks)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById<LinearLayout>(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    companion object {
        private const val KEY_SEARCH_TEXT = "KEY_SEARCH_TEXT"
    }

    private fun createMockedTracks(): ArrayList<Track> = arrayListOf(
        Track(
            trackName = "Smells Like Teen Spirit",
            artistName = "Nirvana",
            trackTime = "5:01",
            artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
        ),
        Track(
            trackName = "Billie Jean",
            artistName = "Michael Jackson",
            trackTime = "4:35",
            artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
        ),
        Track(
            trackName = "Stayin' Alive",
            artistName = "Bee Gees",
            trackTime = "4:10",
            artworkUrl100 = "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
        ),
        Track(
            trackName = "Whole Lotta Love",
            artistName = "Led Zeppelin",
            trackTime = "5:33",
            artworkUrl100 = "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
        ),
        Track(
            trackName = "Sweet Child O'Mine",
            artistName = "Guns N' Roses",
            trackTime = "5:03",
            artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
        )
    )
}
