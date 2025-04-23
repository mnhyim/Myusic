package com.mnhyim.myusic.util

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.mnhyim.myusic.domain.interfaces.ExoPlayerClient
import com.mnhyim.myusic.domain.model.MusicFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
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

    private var currentPlaylist: List<MusicFile> = emptyList()

    private val trackingScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var songPosition: Job? = trackingScope.launch {
        while (exoPlayer.isPlaying) {
            _currentPosition.value = exoPlayer.currentPosition
            delay(1000L)
        }
    }

    private val playerListener: Player.Listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            toggleSongTracking(isPlaying)
            _isPlayingFlow.value = isPlaying
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            val musicFile = mediaItem?.localConfiguration?.tag as? MusicFile
            _currentSong.value = musicFile
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Timber.d("Error: ${error.errorCode} - ${error.cause}")
        }
    }

    private fun toggleSongTracking(status: Boolean) {
        when (status) {
            true -> {
                songPosition?.cancel()
                songPosition = trackingScope.launch {
                    while (exoPlayer.isPlaying) {
                        Timber.d("Current Position: ${exoPlayer.currentPosition}")
                        _currentPosition.value = exoPlayer.currentPosition
                        delay(1000L)
                    }
                }
            }

            else -> {
                songPosition?.cancel()
                songPosition = null
            }
        }
    }

    init {
        exoPlayer.addListener(playerListener)
    }

    override fun setPlaylists(
        musicFiles: List<MusicFile>,
    ) {
        currentPlaylist = musicFiles
        val mediaItems = musicFiles.map { music ->
            MediaItem.Builder()
                .setUri(music.uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(music.name)
                        .setArtist(music.artist)
                        .build()
                )
                .setTag(music)
                .build()
        }
        exoPlayer.setMediaItems(mediaItems)
        exoPlayer.prepare()
    }

    override fun playSong(uri: Uri, musicFile: MusicFile) {
        val index = currentPlaylist.indexOfFirst { it.uri == uri.toString() }

        Timber.d("currentMediaItemIndex: $index")
        when {
            index != -1 -> {
                exoPlayer.seekTo(index, 0)
                exoPlayer.play()
                _currentSong.value = musicFile
            }

            else -> {
                val mediaItem = MediaItem.Builder()
                    .setUri(uri)
                    .setTag(musicFile)
                    .build()
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.play()
                _currentSong.value = musicFile
            }
        }
    }

    override fun pauseSong() = exoPlayer.pause()
    override fun resumeSong() = exoPlayer.play()
    override fun stopSong() = exoPlayer.stop()
    override fun seekForward() = exoPlayer.seekForward()
    override fun seekBackward() = exoPlayer.seekBack()
    override fun playNextSong() = exoPlayer.seekToNext()
    override fun playPrevSong() = exoPlayer.seekToPrevious()
    override fun release() {
        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
        trackingScope.cancel()
    }
}