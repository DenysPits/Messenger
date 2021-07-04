package com.example.messenger.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.messenger.model.db.dao.MessageDao
import com.example.messenger.model.db.dao.UserDao
import com.example.messenger.model.entity.Message
import com.example.messenger.model.entity.User

@Database(entities = [Message::class, User::class], version = 3, exportSchema = false)
abstract class MessengerDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: MessengerDatabase? = null

        fun getDatabase(context: Context): MessengerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MessengerDatabase::class.java,
                    "messenger_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}