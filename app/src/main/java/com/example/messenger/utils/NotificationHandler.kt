/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.messenger.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.messenger.MessengerApplication
import com.example.messenger.R
import com.example.messenger.view.activity.COMPANION_TO_NAVIGATE
import com.example.messenger.view.activity.MainActivity

object NotificationHandler {
    private val notificationManager = ContextCompat.getSystemService(
        MessengerApplication.applicationContext(),
        NotificationManager::class.java
    ) as NotificationManager

    fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    channelId, channelName,
                    NotificationManager.IMPORTANCE_HIGH
                )

            notificationChannel.setShowBadge(false)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Channel for messages"

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun sendNotification(companionId: Long, name: String, message: String, avatar: String) {
        val applicationContext = MessengerApplication.applicationContext()

        val contentIntent = Intent(applicationContext, MainActivity::class.java)
        contentIntent.putExtra(COMPANION_TO_NAVIGATE, companionId)

        val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            companionId.toInt(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.message_notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(name)
            .setContentText(message)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        if (avatar.isNotEmpty()) {
            builder.setLargeIcon(ImageHandler.loadBitmapFromStorage(avatar))
        }

        notificationManager.notify(companionId.toInt(), builder.build())
    }

    fun cancelNotification(companionId: Long) {
        notificationManager.cancel(companionId.toInt())
    }
}
