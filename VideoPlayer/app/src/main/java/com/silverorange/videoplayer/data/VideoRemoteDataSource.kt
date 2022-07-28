package com.silverorange.videoplayer.data

import com.silverorange.videoplayer.api.ApiClient
import com.silverorange.videoplayer.model.Video
import io.reactivex.rxjava3.core.Observable

class VideoRemoteDataSource(api: ApiClient): VideoDataSource {
    private lateinit var fetchVideoCall: Observable<List<Video>>
    private val service = api.build()

    override fun retrieveVideos(callback: FetchVideoCallback<List<Video>>) {
        fetchVideoCall = service!!.getVideos()
        fetchVideoCall.subscribe(
            { value -> callback.onSuccess(value) },
            { error -> callback.onError(error.message) }
        )
    }
}