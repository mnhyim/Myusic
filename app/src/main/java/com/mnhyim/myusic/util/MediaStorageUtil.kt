package com.mnhyim.myusic.util

interface MediaStorageUtil {
    suspend fun fetch(): List<MusicFile>
}