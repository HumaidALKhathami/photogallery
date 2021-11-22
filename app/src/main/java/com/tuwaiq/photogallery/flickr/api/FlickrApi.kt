package com.tuwaiq.photogallery.flickr.api

import com.tuwaiq.photogallery.flickr.modules.FlickrResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {

    // some libraries has their own annotation processor , retrofit is one of them
    @GET("services/rest/?method=flickr.interestingness.getList")
    fun fetchPhotos():Call<FlickrResponse>

    @GET("services/rest/?method=flickr.photos.search")
    fun searchPhotos(@Query("text") query:String):Call<FlickrResponse>

}

