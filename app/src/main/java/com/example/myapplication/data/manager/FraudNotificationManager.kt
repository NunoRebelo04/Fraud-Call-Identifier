package com.example.myapplication.data.manager

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object FraudNotificationManager {

    const val CHANNEL_ID = "fraud_calls_channel"
    const val CHANNEL_NAME = "Fraud Alerts"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alertas de chamadas suspeitas ou spam"
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showBlockedCallNotification(
        context: Context,
        number: String,
        category: String?
    ) {
        val text = if (category != null) {
            "$number bloqueado ($category)"
        } else {
            "$number foi bloqueado"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dialog_alert)
            .setContentTitle("Chamada bloqueada")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(number.hashCode(), notification)
    }
}