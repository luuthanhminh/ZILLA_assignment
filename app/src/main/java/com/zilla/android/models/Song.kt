package com.zilla.android.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Song(val id: String,
           @SerializedName("download_url") val path: String,
           @SerializedName("title") val displayName: String,
           @SerializedName("description") val artist: String,
           @SerializedName("artwork_url") val artworkUrl: String,
           @SerializedName("release_timestamp") val releaseTimestamp: Long
           ) : Serializable {



    var duration: Int = 0

}