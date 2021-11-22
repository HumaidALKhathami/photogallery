package com.tuwaiq.photogallery.flickr.repo


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tuwaiq.photogallery.flickr.api.FlickrApi
import com.tuwaiq.photogallery.flickr.api.PhotoInterceptor
import com.tuwaiq.photogallery.flickr.modules.FlickrResponse
import com.tuwaiq.photogallery.flickr.modules.GalleryItem
import com.tuwaiq.photogallery.flickr.modules.PhotoResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val TAG = "FlickrFetcherRepo"

class FlickrFetcherRepo {

    private val client:OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(PhotoInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.flickr.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val flickrApi = retrofit.create(FlickrApi::class.java)

    fun fetchPhotos():LiveData<List<GalleryItem>> = fetchPhotosMetaData(flickrApi.fetchPhotos())

    fun searchPhotos(query:String):LiveData<List<GalleryItem>> = fetchPhotosMetaData(flickrApi.searchPhotos(query))

    private fun fetchPhotosMetaData(photoRequest: Call<FlickrResponse>):LiveData<List<GalleryItem>>{

        val responseLiveData:MutableLiveData<List<GalleryItem>> = MutableLiveData()



        photoRequest.enqueue(object : Callback<FlickrResponse>{
            override fun onResponse(
                call: Call<FlickrResponse>,
                response: Response<FlickrResponse>
            ) {
                val flickrResponse:FlickrResponse? = response.body()


                val photoResponse:PhotoResponse? = flickrResponse?.photos

                var galleryItem:List<GalleryItem> = photoResponse?.galleryItem ?: emptyList()

                galleryItem = galleryItem.filterNot { it.url.isBlank() }

                responseLiveData.value = galleryItem
            }

            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {

            }

        })

        return responseLiveData
    }
}