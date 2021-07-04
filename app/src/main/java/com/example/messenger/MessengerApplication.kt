package com.example.messenger

import android.app.Application
import com.example.messenger.data.db.MessengerDatabase
import com.example.messenger.data.repository.MessageRepository
import com.example.messenger.data.repository.UserRepository

class MessengerApplication : Application() {
    val database by lazy { MessengerDatabase.getDatabase(this) }
    val userRepository by lazy { UserRepository(database.userDao()) }
    val messageRuntimeException by lazy { MessageRepository(database.messageDao()) }
}