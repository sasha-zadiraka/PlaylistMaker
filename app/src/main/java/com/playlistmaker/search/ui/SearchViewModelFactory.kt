package com.playlistmaker.search.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.playlistmaker.creator.Creator

class SearchViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(
            tracksInteractor = Creator.provideTracksInteractor(),
            searchHistoryInteractor = Creator.provideSearchHistoryInteractor(context.applicationContext)
        ) as T
    }
}