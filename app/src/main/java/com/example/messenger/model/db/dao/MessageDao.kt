package com.example.messenger.model.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.messenger.model.entity.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages WHERE companion_id=:companionId")
    fun getMessagesWithCompanion(companionId: Long): Flow<List<Message>>
}