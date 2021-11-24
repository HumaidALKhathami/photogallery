package com.tuwaiq.photogallery.photogalleryfragment

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle

import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SearchView

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import coil.load
import com.tuwaiq.photogallery.QueryPreferences
import com.tuwaiq.photogallery.R
import com.tuwaiq.photogallery.flickr.modules.GalleryItem
import com.tuwaiq.photogallery.workers.PollWorker

import java.util.concurrent.TimeUnit


const val TAG = "PhotoGalleryFragment"
const val POLL_WORKER = "PollWorker"

class PhotoGalleryFragment : Fragment() {



    private val viewModel by lazy { ViewModelProvider(this)[PhotoGalleryViewModel::class.java] }

    private val onShowNotification = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG,"hi from notification receiver")


            resultCode = Activity.RESULT_CANCELED

        }

    }

    override fun onStart() {
        super.onStart()

        IntentFilter(PollWorker.ACTION_SHOW_NOTIFICATION).also {
            requireContext().registerReceiver(onShowNotification,it, PollWorker.PERM_PRIVATE,null)
        }

    }

    override fun onStop() {
        super.onStop()

        requireContext().unregisterReceiver(onShowNotification)
    }

    private lateinit var photoRv:RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        startNotificationWorker(QueryPreferences.isPolling(requireContext()))

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

        val workRequest = PeriodicWorkRequest
            .Builder(PollWorker::class.java,15,TimeUnit.MINUTES)
            .setConstraints(constraint)
            .build()

        WorkManager.getInstance(requireContext())
            .enqueueUniquePeriodicWork(POLL_WORKER,ExistingPeriodicWorkPolicy.KEEP,workRequest)
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
        val isPollingItem = menu.findItem(R.id.is_polling)

        val isPolling = QueryPreferences.isPolling(requireContext())

        val isPollingItemTitle = if (isPolling){
            R.string.stop_polling
        }else{
            R.string.start_polling
        }
        isPollingItem.setTitle(isPollingItemTitle)

        isPollingItem.setOnMenuItemClickListener {

            startNotificationWorker(isPolling)

//            if (isPolling){
//                WorkManager.getInstance(requireContext()).cancelUniqueWork(POLL_WORKER)
//                QueryPreferences.setPolling(requireContext(),false)
//            }else{
//                startNotificationWorker(false)
//
//                QueryPreferences.setPolling(requireContext(),true)
//            }

            true
        }

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

    private fun startNotificationWorker(isPolling: Boolean) {

        if (isPolling){
            WorkManager.getInstance(requireContext()).cancelUniqueWork(POLL_WORKER)
            QueryPreferences.setPolling(requireContext(),false)
        }else{
            val constraint = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequest
                .Builder(PollWorker::class.java, 15, TimeUnit.MINUTES)
                .setConstraints(constraint)
                .build()


            WorkManager.getInstance(requireContext())
                .enqueueUniquePeriodicWork(
                    POLL_WORKER,
                    ExistingPeriodicWorkPolicy.KEEP,
                    workRequest
                )

            QueryPreferences.setPolling(requireContext(),true)
        }

        activity?.invalidateOptionsMenu()

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