package com.mnhyim.myusic.util

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log

class MediaStorageUtilImpl(
    private val context: Context
) : MediaStorageUtil {

    val collection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST
    )
    val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
    val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

    /* TODO: Exposing a Flow<List<MusicFile>> instead, if I want to auto-update when media DB changes (requires ContentObserver). */
    override suspend fun fetch(): MutableList<MusicFile> {
        val musicFileList = mutableListOf<MusicFile>()

        context.contentResolver.query(collection, projection, selection, null, sortOrder)
            ?.use { cursor ->
                Log.d("MusicUtil", "${cursor.count}")
                val id = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val name = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artist = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(id)
                    val name = cursor.getString(name)
                    val artist = cursor.getString(artist)
                    Log.d("MusicUtil", "[c] $id, $name, $artist")

                    musicFileList += MusicFile(id = id, name = name, artist = artist)
                }

                Log.d("MusicUtil", "musicFileList: $musicFileList")
            }

        return musicFileList
    }
}