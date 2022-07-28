package com.silverorange.videoplayer.data

interface FetchVideoCallback<T> {
    fun onSuccess(data: T?)
    fun onError(error: String?)
}