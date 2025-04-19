package com.mnhyim.myusic.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.mnhyim.myusic.domain.interfaces.ExoPlayerClient
import com.mnhyim.myusic.domain.interfaces.MediaStorageUtil
import com.mnhyim.myusic.util.ExoPlayerClientImpl
import com.mnhyim.myusic.util.MediaStorageUtilImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMediaStorageUtil(
        @ApplicationContext context: Context
    ): MediaStorageUtil {
        return MediaStorageUtilImpl(context)
    }

    @Provides
    @Singleton
    fun provideExoPlayer(
        @ApplicationContext context: Context
    ): ExoPlayer {
        return ExoPlayer.Builder(context).build()
    }

    @Provides
    @Singleton
    fun provideExoPlayerClient(
        exoPlayer: ExoPlayer
    ): ExoPlayerClient {
        return ExoPlayerClientImpl(exoPlayer)
    }

}