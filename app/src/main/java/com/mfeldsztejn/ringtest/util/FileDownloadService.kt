package com.mfeldsztejn.ringtest.util

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mfeldsztejn.ringtest.data.models.Image
import java.io.File
import java.io.FileInputStream

class FileDownloadService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val notification = NotificationManager.createServiceNotification(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(123, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(123, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val image = intent?.getSerializableExtra(IMAGE_EXTRA) as? Image
        val outputFileUri = intent?.getStringExtra(OUTPUT_FILE_URI_EXTRA)
        if (image == null || outputFileUri.isNullOrEmpty()) {
            return START_NOT_STICKY
        }
        Glide.with(applicationContext)
            .downloadOnly()
            .load(image.url)
            .override(image.width, image.height)
            .into(object : CustomTarget<File>() {
                override fun onLoadCleared(placeholder: Drawable?) {}

                override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                    FileInputStream(resource).use { input ->
                        applicationContext.contentResolver
                            .openOutputStream(outputFileUri.toUri())
                            ?.use { output ->
                                input.copyTo(output)
                            }
                    }
                    NotificationManager.showFileDownloadedNotification(applicationContext)
                    stopSelf()
                }

            })
        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        private const val IMAGE_EXTRA = "image_extra"
        private const val OUTPUT_FILE_URI_EXTRA = "output_file_uri_extra"

        fun startService(context: Context, outputFileUri: String, image: Image) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, FileDownloadService::class.java).apply {
                    putExtra(IMAGE_EXTRA, image)
                    putExtra(OUTPUT_FILE_URI_EXTRA, outputFileUri)
                })
        }
    }
}