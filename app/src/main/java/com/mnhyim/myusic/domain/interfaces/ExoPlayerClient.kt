package com.mnhyim.myusic.domain.interfaces

import android.net.Uri
import com.mnhyim.myusic.domain.model.MusicFile
import kotlinx.coroutines.flow.StateFlow

interface ExoPlayerClient {

    val currentSongFlow: StateFlow<MusicFile?>
    val isPlayingFlow: StateFlow<Boolean>

    fun play(uri: Uri, musicFile: MusicFile)
    fun addToQueue(uri: Uri, musicFile: MusicFile)
    fun pause()
    fun resume()
    fun stop()
    fun forward()
    fun backward()
    fun nextSong()
    fun prevSong()
    fun release()
    fun isPlaying(): Boolean
}