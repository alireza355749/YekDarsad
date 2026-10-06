package com.example.yekdarsad.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.yekdarsad.MainActivity
import com.example.yekdarsad.R

object NotificationHelper {

    private const val CHANNEL_ID = "yekdarsad_daily_reminders"

    private const val CHANNEL_NAME = "یادآوری‌های روزانه"

    private const val CHANNEL_DESCRIPTION =
        "یادآوری ثبت غذا و خواب"

    const val EXTRA_NOTIFICATION_TYPE =
        "notification_type"

    fun createChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {

                description = CHANNEL_DESCRIPTION

                enableVibration(true)

                setShowBadge(true)
            }

            val manager =
                context.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    fun showNotification(
        context: Context,
        type: NotificationType
    ) {

        createChannel(context)

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val intent = Intent(
            context,
            MainActivity::class.java
        ).apply {

            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP

            putExtra(
                EXTRA_NOTIFICATION_TYPE,
                type.name
            )
        }

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                type.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(type.title)
                .setContentText(type.message)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(type.message)
                )
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setCategory(
                    NotificationCompat.CATEGORY_REMINDER
                )
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

        val manager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        manager.notify(
            type.id,
            notification
        )
    }
}