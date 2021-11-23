package com.tuwaiq.photogallery.photogalleryfragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SearchView

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import coil.load
import com.tuwaiq.photogallery.R
import com.tuwaiq.photogallery.flickr.modules.GalleryItem
import com.tuwaiq.photogallery.workers.TestWorker


const val TAG = "PhotoGalleryFragment"
class PhotoGalleryFragment : Fragment() {



    private val viewModel by lazy { ViewModelProvider(this)[PhotoGalleryViewModel::class.java] }



    private lateinit var photoRv:RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)



        viewModel.responseLiveData.observe(
            this , Observer { response ->
                Log.d(TAG,"observer $response")
                updateUI(response)
                progressBar.visibility = View.GONE
            }
        )

        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val workRequest = OneTimeWorkRequest
            .Builder(TestWorker::class.java)
            .setConstraints(constraint)
            .build()

        WorkManager.getInstance(requireContext())
            .enqueue(workRequest)
    }

    private fun updateUI (photos : List<GalleryItem>){
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.photo_gallery_fragment,menu)

        val searchItem = menu.findItem(R.id.search_view)
        val searchView = searchItem.actionView as SearchView

        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {

                    if (query != null){
                        viewModel.sendQueryFetchPhotos(query)
                        progressBar.visibility = View.VISIBLE
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // nothing to do here
                    return false
                }

            })

            setOnSearchClickListener {
                searchView.setQuery(viewModel.searchTerm,false)
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.clear_search -> {
                viewModel.sendQueryFetchPhotos("")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


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