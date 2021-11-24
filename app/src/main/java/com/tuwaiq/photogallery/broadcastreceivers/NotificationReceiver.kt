package com.tuwaiq.photogallery.broadcastreceivers

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.tuwaiq.photogallery.workers.PollWorker

private const val TAG = "NotificationReceiver"

class NotificationReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d(TAG,"Received broadcast ${intent?.action}")

        if (resultCode == Activity.RESULT_CANCELED) { return }

            val notification: Notification = intent?.getParcelableExtra(PollWorker.NOTIFICATION)!!

            val notificationManager = NotificationManagerCompat.from(context!!)

            notificationManager.notify(0, notification)

    }
}