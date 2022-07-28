package com.silverorange.videoplayer.model

import com.google.gson.annotations.SerializedName
import java.util.*

// video datamodel
data class Video(
    @SerializedName("id")
    var id: String?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("hlsURL")
    var hlsUrl: String?,
    @SerializedName("fullURL")
    var fullURL: String?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("publishedAt")
    var publishedAt: Date?,
    @SerializedName("author")
    var author: Author?
)
