package com.example.messenger

import android.app.Application
import android.content.Context
import com.example.messenger.model.db.MessengerDatabase
import com.example.messenger.model.repository.ChatPreviewsRepository
import com.example.messenger.model.repository.MessageRepository
import com.example.messenger.model.repository.UserRepository

class MessengerApplication : Application() {

    val database by lazy { MessengerDatabase.getDatabase(this) }
    val userRepository by lazy { UserRepository(database.userDao()) }
    val messageRepository by lazy { MessageRepository(database.messageDao()) }
    val chatPreviewsRepository by lazy { ChatPreviewsRepository(database.chatPreviewsDao()) }

    init {
        instance = this
    }

    companion object {
        private var instance: MessengerApplication? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}