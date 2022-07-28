package com.silverorange.videoplayer.data

import com.silverorange.videoplayer.model.Video

interface VideoDataSource {
    fun retrieveVideos(callback: FetchVideoCallback<List<Video>>)
}