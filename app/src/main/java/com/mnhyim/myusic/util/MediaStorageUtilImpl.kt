package com.mnhyim.myusic.util

import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.core.net.toUri
import com.mnhyim.myusic.domain.interfaces.MediaStorageUtil
import com.mnhyim.myusic.domain.model.MusicFile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class MediaStorageUtilImpl(
    private val context: Context
) : MediaStorageUtil {

    private val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }
    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.ALBUM_ID,
    )

    private val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
    private val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

    private fun hasAlbumArt(uri: Uri): Boolean {
        return try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { true } == true
        } catch (e: Exception) {
            Timber.d("AlbumArt check failed: ${e.message}")
            false
        }
    }

    override fun observeMusicFiles(): Flow<List<MusicFile>> = callbackFlow {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                launch {
                    try {
                        send(queryMusicFiles())
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to fetch music files on change")
                    }
                }
            }
        }

        context.contentResolver.registerContentObserver(
            collection,
            true,
            observer
        )

        launch {
            try {
                send(queryMusicFiles())
            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch initial music files")
            }
        }

        awaitClose {
            context.contentResolver.unregisterContentObserver(observer)
        }
    }

    override suspend fun queryMusicFiles(): MutableList<MusicFile> {
        val musicFileList = mutableListOf<MusicFile>()

        context.contentResolver.query(collection, projection, selection, null, sortOrder)
            ?.use { cursor ->
                Timber.d("Cursor Count: ${cursor.count}")
                val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val albumIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)
                    val name = cursor.getString(nameIndex)
                    val artist = cursor.getString(artistIndex)
                    val duration = cursor.getLong(durationIndex)
                    val albumId = cursor.getLong(albumIdIndex)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    val albumArtUri = ContentUris.withAppendedId(
                        "content://media/external/audio/albumart".toUri(),
                        albumId
                    )
                    Timber.d("Cursor: $id, $name, $artist, $contentUri")

                    val hasAlbumArt = hasAlbumArt(albumArtUri)
                    musicFileList += MusicFile(
                        id = id,
                        name = name,
                        artist = artist,
                        duration = duration,
                        uri = contentUri.toString(),
                        albumArtUri = if (hasAlbumArt) albumArtUri.toString() else null
                    )
                }
            }

        return musicFileList
    }
}

fun formatMilliToMinutes(milli: Long): String {
    val totalSeconds = milli / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}