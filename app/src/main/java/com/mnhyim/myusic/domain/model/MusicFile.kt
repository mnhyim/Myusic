package com.mnhyim.myusic.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MusicFile(
    val id: Long,
    val name: String,
    val artist: String,
    val duration: Long,
    val uri: String,
    val albumArtUri: String? = null
)