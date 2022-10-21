package com.udacity.loadapp.ui

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.udacity.R
import com.udacity.databinding.ActivityMainBinding
import com.udacity.loadapp.util.sendNotification

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.mainLayout.customButton.setOnClickListener {
            val selectedRadioButton = binding.mainLayout.radiogroup.checkedRadioButtonId
            if (selectedRadioButton == -1) {
                Toast.makeText(this, R.string.not_selected_msg, Toast.LENGTH_SHORT).show()
            } else {
                download(selectedRadioButton)
            }
        }

        // create channel notification
        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.app_name)
        )
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (id == downloadID) {
                val notificationManager = ContextCompat.getSystemService(
                    context!!,
                    NotificationManager::class.java
                )
                notificationManager?.sendNotification(downloadID, context!!)
            }
        }
    }

    private fun download(selectedRadioButton: Int) {

        val selectedUrl = when (selectedRadioButton) {
            R.id.glide_radiobutton -> URL_GLIDE
            R.id.loadapp_radiobutton -> URL_UDACITY
            R.id.retrofit_radiobutton -> URL_RETROFIT
            else -> return
        }

        val nameFile = when (selectedRadioButton) {
            R.id.glide_radiobutton -> getString(R.string.radiobutton_glide)
            R.id.loadapp_radiobutton -> getString(R.string.radiobutton_loadapp)
            R.id.retrofit_radiobutton -> getString(R.string.radiobutton_retrofit)
            else -> return
        }

        Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .mkdirs()

        val request = DownloadManager.Request(Uri.parse(selectedUrl))
            .setDescription(nameFile)
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)
            .setDestinationUri(getString(R.string.localtion_file).toUri())

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                setShowBadge(false)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.app_description)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val URL_GLIDE = "https://github.com/bumptech/glide"
        private const val URL_UDACITY =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val URL_RETROFIT = "https://github.com/square/retrofit"
    }

}
