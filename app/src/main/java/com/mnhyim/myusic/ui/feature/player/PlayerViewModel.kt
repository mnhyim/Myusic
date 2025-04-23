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
            MusicControlAction.PauseAction -> exoPlayerClient.pauseSong()
            MusicControlAction.ResumeAction -> exoPlayerClient.resumeSong()
            MusicControlAction.ForwardAction -> exoPlayerClient.seekForward()
            MusicControlAction.BackwardAction -> exoPlayerClient.seekBackward()
            MusicControlAction.NextSongAction -> exoPlayerClient.playNextSong()
            MusicControlAction.PreviousSongAction -> exoPlayerClient.playPrevSong()
        }
    }
}