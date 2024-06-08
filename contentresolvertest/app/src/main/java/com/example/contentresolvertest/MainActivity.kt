package com.example.contentresolvertest

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CallLog
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog

//https://developer.android.com/guide/topics/providers/content-provider-basics

class MainActivity : AppCompatActivity() {
    private val buttonCallLog : Button by lazy {findViewById(R.id.buttonCallLog)}
    private val buttonMedia : Button by lazy {findViewById(R.id.buttonMedia)}
    private val textViewCallLog : TextView by lazy {findViewById(R.id.textViewCallLog)}
    private val textViewImageTitle : TextView by lazy {findViewById(R.id.textViewImageTitle)}
    private val imageView : ImageView by lazy {findViewById(R.id.imageView)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            requestMultiplePermission(arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED))
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            requestMultiplePermission(arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_MEDIA_IMAGES))
        else
            requestMultiplePermission(arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_EXTERNAL_STORAGE))


        buttonCallLog.setOnClickListener {
            readCallLog()
        }

        buttonMedia.setOnClickListener {
            readMedia()
        }
    }

    private fun readCallLog() {
        if (!hasPermission(Manifest.permission.READ_CALL_LOG))
            return
        // query ( URI, projection, selection )
        val projection = arrayOf(CallLog.Calls._ID, CallLog.Calls.NUMBER)
        //val selection =
        val cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, projection, null, null, null)
        val str = StringBuilder()
        cursor?.apply {
            val idxWord = getColumnIndex(CallLog.Calls.NUMBER)
            while (moveToNext()) {
                println(getString(idxWord))
                str.append(getString(idxWord))
                str.append("\n")
            }
            close()
        }
        textViewCallLog.text = str
    }

    private fun readMedia() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (!hasPermission(Manifest.permission.READ_MEDIA_IMAGES) && !hasPermission(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED))
                return
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!hasPermission(Manifest.permission.READ_MEDIA_IMAGES))
                return
        } else {
            if (!hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE))
                return
        }

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.TITLE)
        val cursor = contentResolver.query(collection, projection, null, null, null)
        cursor?.apply {
            val idCol = getColumnIndex(MediaStore.Images.ImageColumns._ID)
            val titleCol = getColumnIndex(MediaStore.Images.ImageColumns.TITLE)
            while (moveToNext()) {
                val contentUri = ContentUris.withAppendedId(
                    collection,
                    getLong(idCol)
                )
                val title = getString(titleCol)
                val bitmap = contentResolver.openInputStream(contentUri)?.use {
                    BitmapFactory.decodeStream(it)
                }
                textViewImageTitle.text = title
                imageView.setImageBitmap(bitmap)
                break // display the first image
            }
            close()
        }
    }

    private fun hasPermission(perm: String) =
        checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED

    private fun requestMultiplePermission(perms: Array<String>) {
        val requestPerms = perms.filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        if (requestPerms.isEmpty())
            return

        val requestPermLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val noPerms = it.filter { item -> !item.value }.keys.toMutableSet()
            val allowedPerms = it.filter { item -> item.value }.keys.toMutableSet()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // READ_MEDIA_VISUAL_USER_SELECTED 와 READ_MEDIA_IMAGES 는 둘 중에 하나만 권한 부여받게 됨
                // 따라서 둘 중에 하나만 있다면 noPerms에 다른 권한은 제거
                if (allowedPerms.contains(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
                    // READ_MEDIA_VISUAL_USER_SELECTED 권한이 true라면 READ_MEDIA_IMAGES는 필요 없으므로 noPerms에서 제거
                    noPerms.remove(Manifest.permission.READ_MEDIA_IMAGES)
                }
                if (allowedPerms.contains(Manifest.permission.READ_MEDIA_IMAGES)) {
                    // READ_MEDIA_IMAGES 권한이 true라면 READ_MEDIA_VISUAL_USER_SELECTED는 필요 없으므로 noPerms에서 제거
                    noPerms.remove(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
                }
            }
            if (noPerms.isNotEmpty()) { // there is a permission which is not granted!
                AlertDialog.Builder(this).apply {
                    setTitle("Warning")
                    setMessage(getString(R.string.no_permission, noPerms.toString()))
                }.show()
            }
        }

        val showRationalePerms = requestPerms.filter {shouldShowRequestPermissionRationale(it)}
        if (showRationalePerms.isNotEmpty()) {
            // you should explain the reason why this app needs the permission.
            AlertDialog.Builder(this).apply {
                setTitle("Reason")
                setMessage(getString(R.string.req_permission_reason, requestPerms))
                setPositiveButton("Allow") { _, _ -> requestPermLauncher.launch(requestPerms.toTypedArray()) }
                setNegativeButton("Deny") { _, _ -> }
            }.show()
        } else {
            // should be called in onCreate()
            requestPermLauncher.launch(requestPerms.toTypedArray())
        }
    }
}