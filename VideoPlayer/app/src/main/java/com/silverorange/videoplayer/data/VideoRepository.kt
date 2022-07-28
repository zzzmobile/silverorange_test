package com.silverorange.videoplayer.data

import com.silverorange.videoplayer.model.Video

class VideoRepository(private val videoDataSource: VideoDataSource) {
    fun fetchVideos(callback: FetchVideoCallback<List<Video>>) {
        videoDataSource.retrieveVideos(callback)
    }
}