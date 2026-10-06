package com.example.yekdarsad.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object NotificationScheduler {

    private const val REQUEST_BREAKFAST = 2001
    private const val REQUEST_LUNCH = 2002
    private const val REQUEST_SLEEP = 2003

    fun scheduleAll(context: Context) {

        scheduleDaily(
            context = context,
            type = NotificationType.BREAKFAST,
            hour = 8,
            minute = 0,
            requestCode = REQUEST_BREAKFAST
        )

        scheduleDaily(
            context = context,
            type = NotificationType.LUNCH,
            hour = 14,
            minute = 0,
            requestCode = REQUEST_LUNCH
        )

        scheduleDaily(
            context = context,
            type = NotificationType.SLEEP,
            hour = 10,
            minute = 0,
            requestCode = REQUEST_SLEEP
        )
    }

    private fun scheduleDaily(
        context: Context,
        type: NotificationType,
        hour: Int,
        minute: Int,
        requestCode: Int
    ) {

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        val intent = Intent(
            context,
            NotificationReceiver::class.java
        ).apply {

            putExtra(
                NotificationHelper.EXTRA_NOTIFICATION_TYPE,
                type.name
            )
        }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val calendar =
            Calendar.getInstance().apply {

                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                if (
                    timeInMillis <=
                    System.currentTimeMillis()
                ) {

                    add(
                        Calendar.DAY_OF_YEAR,
                        1
                    )
                }
            }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}