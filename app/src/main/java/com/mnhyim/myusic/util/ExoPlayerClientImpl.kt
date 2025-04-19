package com.mnhyim.myusic.util

import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.mnhyim.myusic.domain.interfaces.ExoPlayerClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class ExoPlayerClientImpl @Inject constructor(
    private val exoPlayer: ExoPlayer,
) : ExoPlayerClient {

    private val _isPlayingFlow = MutableStateFlow(false)
    override val isPlayingFlow = _isPlayingFlow.asStateFlow()

    private val playerListener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            Log.d("ExoPlayerClient", "e:$error")
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            _isPlayingFlow.value = isPlaying
        }
    }

    init {
        exoPlayer.addListener(playerListener)
    }

    override fun play(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun addToQueue(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer.addMediaItem(mediaItem)
    }

    override fun pause() = exoPlayer.pause()

    override fun resume() = exoPlayer.play()

    override fun stop() = exoPlayer.stop()

    override fun release() = exoPlayer.release()

    override fun isPlaying(): Boolean = exoPlayer.isPlaying
}