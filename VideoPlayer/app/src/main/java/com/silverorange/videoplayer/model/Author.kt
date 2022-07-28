package com.silverorange.videoplayer.model

import com.google.gson.annotations.SerializedName

// author datamodel
data class Author(
    @SerializedName("id")
    var id: String?,
    @SerializedName("name")
    var name: String?
)