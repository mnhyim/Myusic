package com.mnhyim.myusic.ui.feature.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mnhyim.myusic.domain.interfaces.ExoPlayerClient
import com.mnhyim.myusic.util.MusicControlAction
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val exoPlayerClient: ExoPlayerClient,
) : ViewModel() {

    val currentSong = exoPlayerClient.currentSongFlow
    val isPlaying = exoPlayerClient.isPlayingFlow
    val currentPosition = exoPlayerClient.currentPosition

    fun onAction(action: MusicControlAction) {
        when (action) {
            MusicControlAction.PauseAction -> exoPlayerClient.pause()
            MusicControlAction.ResumeAction -> exoPlayerClient.resume()
            MusicControlAction.ForwardAction -> exoPlayerClient.forward()
            MusicControlAction.BackwardAction -> exoPlayerClient.backward()
            MusicControlAction.NextSongAction -> exoPlayerClient.nextSong()
            MusicControlAction.PreviousSongAction -> exoPlayerClient.prevSong()
        }
    }
}