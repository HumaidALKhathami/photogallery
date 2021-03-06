package com.tuwaiq.photogallery.flickr.modules

import com.google.gson.annotations.SerializedName

data class GalleryItem(
    val id:String = "",
    val title :String = "",
    @SerializedName("url_s")
    val url:String ="",
    @SerializedName("owner")
    val userName:String = ""
)