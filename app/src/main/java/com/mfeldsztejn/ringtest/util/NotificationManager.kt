package com.mfeldsztejn.ringtest.util

import android.app.Notification
import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService
import com.mfeldsztejn.ringtest.R
import android.app.NotificationManager as AndroidNotifManager

object NotificationManager {
    private const val CHANNEL_ID = "files_download_channel"
    private const val DOWNLOAD_FILE_NOTIFICATION_ID = 888

    fun registerChannel(context: Context) {
        val notifManager = context.getSystemService<AndroidNotifManager>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notifManager?.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.download_channel),
                    AndroidNotifManager.IMPORTANCE_DEFAULT
                )
            )
        }
    }

    fun createServiceNotification(context: Context): Notification {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(context)
        }.run {
            setContentTitle(context.getString(R.string.downloading))
            build()
        }
    }

    fun showFileDownloadedNotification(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(context)
        }.run {
            setContentTitle(context.getString(R.string.downloaded))
            setSmallIcon(R.drawable.ic_launcher_foreground)
            build()
        }.let {
            context
                .getSystemService<AndroidNotifManager>()
                ?.notify(DOWNLOAD_FILE_NOTIFICATION_ID, it)
        }
    }
}