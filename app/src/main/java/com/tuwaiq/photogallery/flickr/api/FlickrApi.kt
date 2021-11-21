package com.tuwaiq.photogallery.flickr.api

import com.tuwaiq.photogallery.flickr.modules.FlickrResponse
import retrofit2.Call
import retrofit2.http.GET

interface FlickrApi {

    // some libraries has their own annotation processor , retrofit is one of them
    @GET("services/rest/?method=flickr.interestingness.getList" +
            "&api_key=d0c796986bdd60cf633bce7affcefdae" +
            "&extras=url_s" +
            "&format=json" +
            "&nojsoncallback=1")
    fun fetchPhotos():Call<FlickrResponse>
}

