package com.tuwaiq.photogallery.photogalleryfragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tuwaiq.photogallery.R
import com.tuwaiq.photogallery.flickr.modules.GalleryItem


const val TAG = "PhotoGalleryFragment"
class PhotoGalleryFragment : Fragment() {



    private val viewModel by lazy { ViewModelProvider(this)[PhotoGalleryViewModel::class.java] }

    private lateinit var photoRv:RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.responseLiveData.observe(
            this , Observer { response ->
                Log.d(TAG,"observer $response")
                updateUI(response)
                progressBar.visibility = View.GONE
            }
        )


    }

    fun updateUI (photos : List<GalleryItem>){
        photoRv.adapter = PhotoAdapter(photos)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.photo_gallery_fragment, container, false)


        photoRv = view.findViewById(R.id.photo_rv)
        progressBar = view.findViewById(R.id.progress_bar)

        photoRv.layoutManager = GridLayoutManager(context,3)
        return view
    }



    private inner class PhotoViewHolder (view: View): RecyclerView.ViewHolder(view){

        private val photoIv: ImageView = view.findViewById(R.id.photo_item)


        fun bind(photo: GalleryItem){

            photoIv.load(photo.url){
                placeholder(R.drawable.ic_baseline_cloud_download_24)
                crossfade(300)
            }


        }

    }

    private inner class PhotoAdapter(val photos : List<GalleryItem>) :
        RecyclerView.Adapter<PhotoViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
            val view = layoutInflater.inflate(R.layout.photo_gallery_item,parent,false)

            return PhotoViewHolder(view)
        }

        override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
            val photo = photos[position]

            holder.bind(photo)
        }

        override fun getItemCount(): Int = photos.size

    }

}