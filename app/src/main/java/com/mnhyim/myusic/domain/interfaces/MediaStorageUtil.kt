package com.mnhyim.myusic.domain.interfaces

import com.mnhyim.myusic.domain.model.MusicFile
import kotlinx.coroutines.flow.Flow

interface MediaStorageUtil {
    fun observeMusicFiles(): Flow<List<MusicFile>>
    suspend fun queryMusicFiles(): List<MusicFile>
}