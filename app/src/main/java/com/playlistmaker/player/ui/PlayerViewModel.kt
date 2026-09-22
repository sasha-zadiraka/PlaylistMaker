package com.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlistmaker.medialibrary.domain.FavoriteTracksInteractor
import com.playlistmaker.player.service.PlaybackServiceContract
import com.playlistmaker.player.service.PlaybackStatus
import com.playlistmaker.playlist.domain.Playlist
import com.playlistmaker.playlist.domain.PlaylistInteractor
import com.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData(PlayerState())

    fun observeState(): LiveData<PlayerState> = stateLiveData

    private val playlistsLiveData =
        MutableLiveData<List<Playlist>>(emptyList())

    fun observePlaylists(): LiveData<List<Playlist>> =
        playlistsLiveData

    private val addTrackResultLiveData =
        MutableLiveData<AddTrackToPlaylistResult>()

    fun observeAddTrackResult(): LiveData<AddTrackToPlaylistResult> =
        addTrackResultLiveData

    private var currentTrack: Track? = null

    private var playbackService: PlaybackServiceContract? = null
    private var stateCollectionJob: Job? = null

    fun setTrack(track: Track) {
        currentTrack = track

        stateLiveData.value = stateLiveData.value?.copy(
            isFavorite = track.isFavorite
        )
    }

    fun attachService(service: PlaybackServiceContract) {
        playbackService = service

        stateCollectionJob?.cancel()
        stateCollectionJob = viewModelScope.launch {
            service.observeState().collect { playbackState ->
                stateLiveData.value = stateLiveData.value?.copy(
                    isPrepared = playbackState.status != PlaybackStatus.DEFAULT,
                    isPlaying = playbackState.status == PlaybackStatus.PLAYING,
                    progress = formatTime(playbackState.progress)
                )
            }
        }
    }

    fun detachService() {
        stateCollectionJob?.cancel()
        stateCollectionJob = null
        playbackService = null
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            val playlists = playlistInteractor
                .getPlaylists()
                .first()

            playlistsLiveData.value = playlists
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        val track = currentTrack ?: return

        if (playlist.trackIds.contains(track.trackId)) {
            addTrackResultLiveData.value =
                AddTrackToPlaylistResult.AlreadyAdded(
                    playlistName = playlist.name
                )

            return
        }

        viewModelScope.launch {
            runCatching {
                playlistInteractor.addTrackToPlaylist(
                    track = track,
                    playlist = playlist
                )
            }
                .onSuccess {
                    addTrackResultLiveData.value =
                        AddTrackToPlaylistResult.Added(
                            playlistName = playlist.name
                        )
                }
                .onFailure {
                    addTrackResultLiveData.value =
                        AddTrackToPlaylistResult.Error
                }
        }
    }

    fun onFavoriteClicked() {
        val track = currentTrack ?: return
        val isFavorite = stateLiveData.value?.isFavorite ?: false

        viewModelScope.launch {
            if (isFavorite) {
                favoriteTracksInteractor.deleteTrack(track)
            } else {
                favoriteTracksInteractor.addTrack(track)
            }

            track.isFavorite = !isFavorite

            stateLiveData.value = stateLiveData.value?.copy(
                isFavorite = !isFavorite
            )
        }
    }

    fun playbackControl() {
        val service = playbackService ?: return
        val currentState = stateLiveData.value ?: return

        if (!currentState.isPrepared) {
            return
        }

        if (currentState.isPlaying) {
            service.pause()
        } else {
            service.play()
        }
    }

    fun onAppBackgrounded() {
        if (stateLiveData.value?.isPlaying == true) {
            playbackService?.showNotification()
        }
    }

    fun onAppForegrounded() {
        playbackService?.hideNotification()
    }

    private fun formatTime(position: Int): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(position)
    }

    override fun onCleared() {
        super.onCleared()
        detachService()
    }
}
