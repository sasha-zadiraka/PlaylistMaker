package com.playlistmaker.player.domain

class PlayerInteractorImpl(
    private val repository: PlayerRepository
) : PlayerInteractor {

    override fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        repository.prepare(url, onPrepared, onCompletion)
    }

    override fun start() {
        repository.start()
    }

    override fun pause() {
        repository.pause()
    }

    override fun release() {
        repository.release()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }

    override fun isPlaying(): Boolean {
        return repository.isPlaying()
    }
}