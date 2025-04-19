package com.mnhyim.myusic.ui.feature.home

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnhyim.myusic.domain.interfaces.ExoPlayerClient
import com.mnhyim.myusic.domain.interfaces.MediaStorageUtil
import com.mnhyim.myusic.domain.model.MusicFile
import com.mnhyim.myusic.util.MusicControlAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mediaStorageUtil: MediaStorageUtil,
    private val exoPlayerClient: ExoPlayerClient
) : ViewModel() {

    private var _musicList = MutableStateFlow(emptyList<MusicFile>())
    val musicList = _musicList.asStateFlow()

    val isPlaying = exoPlayerClient.isPlayingFlow

    init {
        Log.d("HomeViewModel", "ViewModel Initialized")
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayerClient.release()
    }

    fun fetchMusic() {
        viewModelScope.launch {
            _musicList.value = mediaStorageUtil.fetch()
        }
    }

    fun playTrack(uri: Uri) {
        exoPlayerClient.play(uri)
    }

    fun onAction(action: MusicControlAction) {
        when (action) {
            MusicControlAction.PauseAction -> exoPlayerClient.pause()
            MusicControlAction.ForwardAction -> {}
            MusicControlAction.BackwardAction -> {}
            MusicControlAction.ResumeAction -> exoPlayerClient.resume()
        }
    }
}