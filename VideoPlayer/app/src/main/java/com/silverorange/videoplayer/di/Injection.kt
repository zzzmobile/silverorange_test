package com.silverorange.videoplayer.di

import androidx.lifecycle.ViewModelProvider
import com.silverorange.videoplayer.api.ApiClient
import com.silverorange.videoplayer.data.VideoDataSource
import com.silverorange.videoplayer.data.VideoRemoteDataSource
import com.silverorange.videoplayer.data.VideoRepository
import com.silverorange.videoplayer.viewmodel.ViewModelFactory

object Injection {
    private val dataSource: VideoDataSource = VideoRemoteDataSource(ApiClient)
    private val repository = VideoRepository(dataSource)
    private val viewModelFactory = ViewModelFactory(repository)

    fun provideViewModelFactory(): ViewModelProvider.Factory {
        return viewModelFactory
    }
}