package com.tuwaiq.photogallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tuwaiq.photogallery.photogalleryfragment.PhotoGalleryFragment

class PhotoGalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_gallery)
    }
}