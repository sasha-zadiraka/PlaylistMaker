package com.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlistmaker.search.domain.SearchHistoryInteractor
import com.playlistmaker.search.domain.TracksInteractor
import com.playlistmaker.search.domain.models.Track
import com.playlistmaker.util.AppConstants.SEARCH_DEBOUNCE_DELAY
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var latestSearchText = ""

    private var searchJob: Job? = null

    fun onSearchTextChanged(text: String) {
        latestSearchText = text

        searchJob?.cancel()

        if (text.isEmpty()) {
            stateLiveData.value = SearchState.NothingFound
            showHistoryIfNeeded()
        } else {
            stateLiveData.value = SearchState.NothingFound
            searchDebounce()
        }
    }

    fun searchImmediately(text: String) {
        searchJob?.cancel()
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
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(latestSearchText)
        }
    }

    private fun searchRequest(text: String) {
        val query = text.trim()

        if (query.isBlank()) return

        searchJob = viewModelScope.launch {
            stateLiveData.value = SearchState.Loading

            tracksInteractor.searchTracks(query)
                .collect { tracks ->
                    when {
                        tracks == null -> {
                            stateLiveData.value = SearchState.Error
                        }

                        tracks.isEmpty() -> {
                            stateLiveData.value = SearchState.Empty
                        }

                        else -> {
                            stateLiveData.value = SearchState.Content(tracks)
                        }
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }

    fun hasState(): Boolean {
        return stateLiveData.value != null
    }

    fun getLatestSearchText(): String {
        return latestSearchText
    }
}