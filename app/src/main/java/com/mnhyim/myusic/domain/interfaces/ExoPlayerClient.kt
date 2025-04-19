package com.mnhyim.myusic.domain.interfaces

import android.net.Uri
import kotlinx.coroutines.flow.StateFlow

interface ExoPlayerClient {

    val isPlayingFlow: StateFlow<Boolean>
    fun play(uri: Uri)
    fun addToQueue(uri: Uri)
    fun pause()
    fun resume()
    fun stop()
    fun release()
    fun isPlaying(): Boolean
}