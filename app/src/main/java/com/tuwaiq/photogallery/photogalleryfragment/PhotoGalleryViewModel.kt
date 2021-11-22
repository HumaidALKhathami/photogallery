package com.tuwaiq.photogallery.photogalleryfragment

import android.app.Application
import androidx.lifecycle.*
import com.tuwaiq.photogallery.QueryPreferences
import com.tuwaiq.photogallery.flickr.modules.GalleryItem
import com.tuwaiq.photogallery.flickr.repo.FlickrFetcherRepo

class PhotoGalleryViewModel(private val app: Application) : AndroidViewModel(app) {

    private val flickrFetcherRepo = FlickrFetcherRepo()

    var responseLiveData: LiveData<List<GalleryItem>>

    private val liveDataSearchTerm: MutableLiveData<String> = MutableLiveData()

    val searchTerm: String
        get() = QueryPreferences.getStoredQuery(app)

    init {

        liveDataSearchTerm.value = searchTerm

        responseLiveData = Transformations.switchMap(liveDataSearchTerm) { searchTerm ->
            if (searchTerm.isNotBlank()) {
                flickrFetcherRepo.searchPhotos(searchTerm)

            } else {
                flickrFetcherRepo.fetchPhotos()
            }
        }
    }

        fun sendQueryFetchPhotos(query: String) {

            liveDataSearchTerm.value = query

            QueryPreferences.setStoredQuery(app, query)

        }

    }
