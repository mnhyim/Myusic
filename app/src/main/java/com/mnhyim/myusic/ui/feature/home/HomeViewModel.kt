package com.mnhyim.myusic.ui.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnhyim.myusic.util.MediaStorageUtil
import com.mnhyim.myusic.util.MusicFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mediaStorageUtil: MediaStorageUtil
): ViewModel() {

    private var _musicList = MutableStateFlow(emptyList<MusicFile>())
    val musicList = _musicList.asStateFlow()

    init {
        Log.d("HomeViewModel", "ViewModel Initialized")
    }

    fun fetchMusic() {
        viewModelScope.launch {
            _musicList.value = mediaStorageUtil.fetch()
        }
    }

}