package com.udacity.loadapp.util

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.R
import com.udacity.loadapp.ui.DetailActivity

private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(downloadId: Long, applicationContext: Context) {
    // Create the content intent for the notification, which launches this activity
    val detailIntent = Intent(applicationContext, DetailActivity::class.java)
    detailIntent.putExtra(DownloadManager.EXTRA_DOWNLOAD_ID, downloadId)

    // create PendingIntent
    val detailPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        detailIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(applicationContext.getString(R.string.notification_description))
        //.setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .addAction(
            0,
            applicationContext.getString(R.string.notification_button),
            detailPendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}