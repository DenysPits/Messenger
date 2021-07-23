package com.example.messenger.model.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.messenger.model.entity.ChatPreview
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatPreviewsDao {

    @Query(
        """
        SELECT users.id AS user_id, name, avatar, text AS message, time FROM users 
        LEFT JOIN messages ON users.id=messages.companion_id
        JOIN (SELECT companion_id, MAX(time) AS MaxTime FROM messages GROUP BY companion_id) messages_group 
        ON messages.companion_id = messages_group.companion_id AND messages.time = messages_group.MaxTime
        WHERE is_my_user = 0
        UNION SELECT users.id AS user_id, name, avatar, text AS message, time FROM users
        LEFT JOIN messages ON users.id=messages.companion_id
        WHERE is_my_user = 0 AND message IS NULL"""
    )
    fun getChatPreviews(): Flow<List<ChatPreview>>
}