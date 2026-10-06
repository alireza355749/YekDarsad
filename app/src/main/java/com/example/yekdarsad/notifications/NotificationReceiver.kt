package com.example.yekdarsad.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val typeName =
            intent.getStringExtra(
                NotificationHelper.EXTRA_NOTIFICATION_TYPE
            )

        val type =
            typeName?.let {
                runCatching {
                    NotificationType.valueOf(it)
                }.getOrNull()
            }
                ?: return

        NotificationHelper.showNotification(
            context,
            type
        )
    }
}