package com.udacity.loadapp.ui

import android.app.DownloadManager
import android.app.NotificationManager
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.udacity.R
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private var downloadStatus = DownloadManager.STATUS_FAILED
    private var downloadDescription = ""
    private var downloadFile = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val notificationManager = getSystemService(
            NotificationManager::class.java
        )
        notificationManager.cancelAll()

        if (null != intent) {
            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
            queryLastDownload(downloadId)
        }

        doWebViewPrint()

    }

    private fun queryLastDownload(downloadId: Long) {
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val cursor: Cursor =
            downloadManager.query(DownloadManager.Query().setFilterById(downloadId))

        cursor.moveToFirst()
        downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
        downloadDescription =
            cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION))
        downloadFile = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))

        cursor.close()
    }

    override fun onResume() {
        super.onResume()

        var downloadStatusText = ""
        when (downloadStatus) {
            DownloadManager.STATUS_SUCCESSFUL -> downloadStatusText =
                getString(R.string.status_success)
            DownloadManager.STATUS_FAILED -> downloadStatusText = getString(R.string.status_failed)
        }
        binding.detailLayout.statusTextview.text = downloadStatusText
        binding.detailLayout.fileNameTextview.text = downloadDescription
    }

    private fun doWebViewPrint() {
        // Create a WebView object specifically for printing
        val webView = binding.detailLayout.webview
        webView.settings.allowContentAccess = true
        webView.settings.allowFileAccess = true
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) =
                false

            override fun onPageFinished(view: WebView, url: String) {
                Log.i("Detail view", "page finished loading $url")
            }
        }
        webView.loadUrl(downloadFile)
    }

}
