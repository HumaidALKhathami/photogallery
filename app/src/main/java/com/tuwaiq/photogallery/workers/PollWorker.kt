package com.tuwaiq.photogallery.workers

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.tuwaiq.photogallery.*
import com.tuwaiq.photogallery.flickr.modules.GalleryItem
import com.tuwaiq.photogallery.flickr.repo.FlickrFetcherRepo

private const val TAG = "PollWorker"

class PollWorker(private val context: Context, workerPrams : WorkerParameters) : Worker(context,workerPrams) {
    override fun doWork(): Result {

        val query = QueryPreferences.getStoredQuery(context)

        val lastResultId = QueryPreferences.getLastResultId(context)

        val items:List<GalleryItem> = if (query.isEmpty()){

            FlickrFetcherRepo().fetchPhotosRequest().execute()
                .body()
                ?.photos
                ?.galleryItem
        }else{
            FlickrFetcherRepo().searchPhotosRequest(query).execute()
                .body()
                ?.photos
                ?.galleryItem
        }   ?: emptyList()

        if (items.isEmpty()) {
            return Result.success()
        }

        val resultId = items.first().id

        if (resultId == lastResultId){
            Log.d(TAG,"no new photos")
        }else{
            Log.d(TAG,"there are new photos!!")
            QueryPreferences.setLastResultId(context,lastResultId)

            notification()


        }

        return Result.success()
    }

    companion object{
        const val ACTION_SHOW_NOTIFICATION = "com.tuwaiq,photogallery.SHOW_NOTIFICATION_ACTION"
        const val PERM_PRIVATE = "com.tuwaiq.photogallery.PRIVATE"

        const val NOTIFICATION = "notify"

    }

    private fun notification(){
        val resources = context.resources

        val intent = PhotoGalleryActivity.newIntent(context)

        val pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, CHANNEL_NOTIFICATION_ID)
            .setTicker(resources.getString(R.string.new_pictures_title))
            .setSmallIcon(R.drawable.ic_baseline_directions_bike_24)
            .setContentTitle(resources.getString(R.string.new_pictures_title))
            .setContentText(resources.getString(R.string.new_pictures_text))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

//        val notificationManager = NotificationManagerCompat.from(context)

        showBackgroundNotification(notification)

//        notificationManager.notify(0,notification)
    }

    private fun showBackgroundNotification(notification: Notification){

        val intent = Intent(ACTION_SHOW_NOTIFICATION).apply {
            putExtra(NOTIFICATION,notification)
        }

        context.sendOrderedBroadcast(intent, PERM_PRIVATE)
    }

}