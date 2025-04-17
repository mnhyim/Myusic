package com.mnhyim.myusic.di

import android.content.Context
import com.mnhyim.myusic.util.MediaStorageUtil
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
}