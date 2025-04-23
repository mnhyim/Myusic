package com.mnhyim.myusic.domain.interfaces

import android.net.Uri
import com.mnhyim.myusic.domain.model.MusicFile
import kotlinx.coroutines.flow.StateFlow

interface ExoPlayerClient {
    val isPlayingFlow: StateFlow<Boolean>
    val currentSongFlow: StateFlow<MusicFile?>
    val currentPosition: StateFlow<Long>

    fun setPlaylists(musicFiles: List<MusicFile>)
    fun playSong(uri: Uri, musicFile: MusicFile)
    fun pauseSong()
    fun resumeSong()
    fun stopSong()
    fun seekForward()
    fun seekBackward()
    fun playNextSong()
    fun playPrevSong()
    fun release()
}