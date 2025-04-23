package com.mnhyim.myusic.ui.feature.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnhyim.myusic.domain.interfaces.ExoPlayerClient
import com.mnhyim.myusic.domain.interfaces.MediaStorageUtil
import com.mnhyim.myusic.domain.model.MusicFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mediaStorageUtil: MediaStorageUtil,
    private val exoPlayerClient: ExoPlayerClient
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    private var _musicList = mediaStorageUtil
        .observeMusicFiles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
    val musicList = _musicList

    init {
        viewModelScope.launch {
            musicList.collectLatest { list ->
                if (list.isNotEmpty()) {
                    exoPlayerClient.setPlaylists(list)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayerClient.release()
    }

    fun playTrack(uri: Uri, musicFile: MusicFile) {
        exoPlayerClient.playSong(uri, musicFile)
    }

    fun addToQueue(uri: Uri, musicFile: MusicFile) {
//        exoPlayerClient.addToQueue(uri, musicFile)
    }
}