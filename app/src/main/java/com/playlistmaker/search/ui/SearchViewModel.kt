package com.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.playlistmaker.search.domain.SearchHistoryInteractor
import com.playlistmaker.search.domain.TracksInteractor
import com.playlistmaker.search.domain.models.Track
import com.playlistmaker.util.AppConstants.SEARCH_DEBOUNCE_DELAY

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val handler = Handler(Looper.getMainLooper())

    private var latestSearchText = ""

    private val searchRunnable = Runnable {
        searchRequest(latestSearchText)
    }

    fun onSearchTextChanged(text: String) {
        latestSearchText = text

        handler.removeCallbacks(searchRunnable)

        if (text.isEmpty()) {
            stateLiveData.value = SearchState.NothingFound
            showHistoryIfNeeded()
        } else {
            stateLiveData.value = SearchState.NothingFound
            searchDebounce()
        }
    }

    fun searchImmediately(text: String) {
        handler.removeCallbacks(searchRunnable)
        latestSearchText = text

        if (text.isBlank()) {
            stateLiveData.value = SearchState.NothingFound
            showHistoryIfNeeded()
            return
        }

        searchRequest(text)
    }

    fun showHistoryIfNeeded() {
        val history = searchHistoryInteractor.getHistory()

        if (latestSearchText.isEmpty() && history.isNotEmpty()) {
            stateLiveData.value = SearchState.History(history)
        } else {
            stateLiveData.value = SearchState.NothingFound
        }
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        stateLiveData.value = SearchState.NothingFound
    }

    fun saveTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrack(track)
    }

    private fun searchDebounce() {
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun searchRequest(text: String) {
        val query = text.trim()

        if (query.isBlank()) return

        stateLiveData.postValue(SearchState.Loading)

        tracksInteractor.searchTracks(query) { tracks ->
            when {
                tracks == null -> {
                    stateLiveData.postValue(SearchState.Error)
                }

                tracks.isEmpty() -> {
                    stateLiveData.postValue(SearchState.Empty)
                }

                else -> {
                    stateLiveData.postValue(SearchState.Content(tracks))
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(searchRunnable)
    }
}