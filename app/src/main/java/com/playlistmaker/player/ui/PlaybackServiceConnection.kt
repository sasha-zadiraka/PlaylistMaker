package com.playlistmaker.player.ui

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.playlistmaker.player.service.PlaybackService

class PlaybackServiceConnection(
    private val viewModel: PlayerViewModel
) : ServiceConnection {

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        val binder = service as PlaybackService.PlaybackBinder
        viewModel.attachService(binder.getService())
    }

    override fun onServiceDisconnected(name: ComponentName) {
        viewModel.detachService()
    }
}
