package com.example.messenger

import android.app.Application
import com.example.messenger.model.db.MessengerDatabase
import com.example.messenger.model.repository.MessageRepository
import com.example.messenger.model.repository.UserRepository

class MessengerApplication : Application() {
    val database by lazy { MessengerDatabase.getDatabase(this) }
    val userRepository by lazy { UserRepository(database.userDao()) }
    val messageRepository by lazy { MessageRepository(database.messageDao()) }
}