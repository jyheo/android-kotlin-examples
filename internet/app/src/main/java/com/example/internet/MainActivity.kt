package com.example.internet

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider.getUriForFile
import com.google.android.material.snackbar.Snackbar
import java.io.File

class MainActivity : AppCompatActivity() {
    private val output by lazy { findViewById<TextView>(R.id.textView) }
    private val outputBy by lazy { findViewById<TextView>(R.id.textViewBy) }
    private val outputImg by lazy { findViewById<ImageView>(R.id.imageView) }
    private val viewModel : MyViewModel by viewModels {
        MyViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (isNetworkAvailable())
            Snackbar.make(output, "Network available", Snackbar.LENGTH_SHORT).show()
        else
            Snackbar.make(output, "Network unavailable", Snackbar.LENGTH_SHORT).show()

        findViewById<Button>(R.id.java_socket).setOnClickListener {
            viewModel.refreshJavaSocket()
        }
        findViewById<Button>(R.id.java_http).setOnClickListener {
            val downloadURL = "https://www.hansung.ac.kr/sites/hansung/images/sub/ui_10.png"
            viewModel.refreshBitmapByHttpsLib(downloadURL)
        }
        findViewById<Button>(R.id.volley).setOnClickListener {
            val downloadURL = "https://www.hansung.ac.kr/sites/hansung/images/sub/%EC%83%81%EC%83%81%EB%B6%80%EA%B8%B0%ED%94%84%EB%A0%8C%EC%A6%88.jpg"
            viewModel.refreshBitmapByVolley(downloadURL)
        }
        findViewById<Button>(R.id.retrofit).setOnClickListener {
            viewModel.refreshRetrofit("jyheo")
        }
        findViewById<Button>(R.id.saveBitmap).setOnClickListener {
            viewModel.responseImg.value ?. let {
                saveBitmap(it, "bitmap.png")
            }
        }

        viewModel.responseBy.observe(this) {
            outputBy.text = it
        }
        viewModel.response.observe(this) {
            output.text = it
        }
        viewModel.responseImg.observe(this) {
            outputImg.setImageBitmap(it)
        }

        findViewById<Button>(R.id.download).setOnClickListener { downloadManager() }
        findViewById<Button>(R.id.openDownload).setOnClickListener { openDownload() }


    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        println("${actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)}, ${actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)}, " +
                "${actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)}")
        return actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

    }

    private fun saveBitmap(bitmap : Bitmap, fileName : String) {
        // Bitmap 을 MediaStore 에 저장, contentResolver 사용
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        }
        val destUri = contentResolver.insert(collection, contentValues) ?: return
        contentResolver.openOutputStream(destUri)?.let {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            it.close()
        }
    }

    private fun httpLib() {
        val downloadURL = "https://www.hansung.ac.kr/sites/hansung/images/common/logo.png"
        viewModel.refreshBitmapByHttpsLib(downloadURL)
    }

    private fun volley() {
        val downloadURL = "https://www.hansung.ac.kr/sites/hansung/images/common/logo.png"
        viewModel.refreshBitmapByVolley(downloadURL)
    }

    private var downloadId = -1L
    private val dwonloadFile = "testdownload.png"

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val did = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (did != downloadId) return
            when (intent.action) {
                DownloadManager.ACTION_DOWNLOAD_COMPLETE -> {
                    val dManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    val pfd = dManager.openDownloadedFile(downloadId)
                    ParcelFileDescriptor.AutoCloseInputStream(pfd).use {
                        viewModel.responseBy.value = "Download Manager"
                        viewModel.response.value = dManager.getUriForDownloadedFile(downloadId).path
                        viewModel.responseImg.value = BitmapFactory.decodeStream(it)
                    }
                    println("Download Completed!")
                }
                else -> {
                    // DownloadManager.ACTION_NOTIFICATION_CLICKED
                    // DownloadManager.ACTION_VIEW_DOWNLOADS
                    println(intent.action)
                }
            }
        }

    }

    private fun downloadManager() {
        val downloadURL = Uri.parse("https://www.hansung.ac.kr/sites/hansung/images/common/logo.png")

        val iFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        ContextCompat.registerReceiver(this, receiver, iFilter, ContextCompat.RECEIVER_EXPORTED)

        val dManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(downloadURL).apply {
            setTitle("Download")
            setDescription("Downloading a File")
            // setRequiresDeviceIdle(true)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalFilesDir(baseContext, Environment.DIRECTORY_DOWNLOADS, dwonloadFile)
            // setAllowedOverMetered(false)
        }
        downloadId = dManager.enqueue(request)
        // you can use the dID for removing/deleting the download. dManager.remove(dID)
    }

    private fun openDownload() {
        val filePath = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), dwonloadFile)
        val contentUri: Uri =
            getUriForFile(this, "com.example.internet.file_provider", filePath)
        // we need <provider> in AndroidManifest.xml for the getUriForFile
        println(contentUri)
        val i = Intent().apply {
            action = Intent.ACTION_VIEW
            data = contentUri
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(i)
    }
}