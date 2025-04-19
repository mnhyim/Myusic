package com.mnhyim.myusic.domain.interfaces

import com.mnhyim.myusic.domain.model.MusicFile

interface MediaStorageUtil {
    suspend fun fetch(): List<MusicFile>
}