package com.silverorange.videoplayer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.silverorange.videoplayer.data.FetchVideoCallback
import com.silverorange.videoplayer.data.VideoRepository
import com.silverorange.videoplayer.model.Video

class MainViewModel(private val repository: VideoRepository): ViewModel() {
    private val _videos = MutableLiveData<List<Video>>()
    val videos: LiveData<List<Video>> = _videos     // fetched videos

    private val _error = MutableLiveData<Any>()
    val error: LiveData<Any> = _error   // fetch error

    fun getVideos() {
        repository.fetchVideos(object : FetchVideoCallback<List<Video>> {
            override fun onSuccess(data: List<Video>?) {
                _videos.postValue(data)
            }

            override fun onError(error: String?) {
                _error.postValue(error)
            }
        })
    }
}