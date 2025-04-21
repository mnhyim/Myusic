package com.mnhyim.myusic.util

import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.mnhyim.myusic.domain.interfaces.ExoPlayerClient
import com.mnhyim.myusic.domain.model.MusicFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ExoPlayerClientImpl @Inject constructor(
    private val exoPlayer: ExoPlayer,
) : ExoPlayerClient {

    private val _isPlayingFlow = MutableStateFlow(false)
    override val isPlayingFlow = _isPlayingFlow.asStateFlow()

    private val _currentSong: MutableStateFlow<MusicFile?> = MutableStateFlow(null)
    override val currentSongFlow = _currentSong.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition: StateFlow<Long> get() = _currentPosition

    private var positionJob: Job? = null

    private val playerListener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            Log.d("ExoPlayerClient", "e:$error")
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (isPlaying) {
                startTracking()
            } else {
                stopTracking()
            }
            _isPlayingFlow.value = isPlaying
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)

            val musicFile = mediaItem?.localConfiguration?.tag as? MusicFile
            _currentSong.value = musicFile
        }
    }

    init {
        exoPlayer.addListener(playerListener)
    }

    override fun play(uri: Uri, musicFile: MusicFile) {
        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .setTag(musicFile)
            .build()
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
        _currentSong.value = musicFile
        startTracking()
    }

    override fun addToQueue(uri: Uri, musicFile: MusicFile) {
        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .setTag(musicFile)
            .build()
        exoPlayer.addMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    override fun pause() = exoPlayer.pause()

    override fun resume() = exoPlayer.play()

    override fun stop() = exoPlayer.stop()

    override fun forward() = exoPlayer.seekForward()

    override fun backward() = exoPlayer.seekBack()

    override fun nextSong() = exoPlayer.seekToNext()

    override fun prevSong() = exoPlayer.seekToPrevious()

    override fun release() = exoPlayer.release()

    private fun startTracking() {
        positionJob?.cancel()
        positionJob = CoroutineScope(Dispatchers.Main).launch {
            while (exoPlayer.isPlaying) {
                Log.d("ExoPlayerClientImpl", "cp: ${exoPlayer.currentPosition}")
                _currentPosition.value = exoPlayer.currentPosition
                delay(1000L)
            }
        }
    }

    private fun stopTracking() {
        positionJob?.cancel()
        positionJob = null
    }
}