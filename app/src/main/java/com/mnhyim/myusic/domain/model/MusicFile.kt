package com.mnhyim.myusic.domain.model

data class MusicFile(
    val id: Long,
    val name: String,
    val artist: String,
    val uri: String,
    val albumArtUri: String? = null
)