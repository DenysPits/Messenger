package com.example.messenger.model.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.messenger.model.entity.ChatPreview
import com.example.messenger.model.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun save(user: User)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun countRows(): Int

    @Query("""
        SELECT users.id AS user_id, name, avatar, text AS message, time FROM users 
        LEFT JOIN messages ON users.id=messages.companion_id 
        JOIN (SELECT companion_id, MAX(time) AS MaxTime FROM messages GROUP BY companion_id) messages_group 
        ON CASE 
        WHEN messages.text IS NULL THEN 1
        ELSE messages.companion_id = messages_group.companion_id AND messages.time = messages_group.MaxTime
        END 
        WHERE is_my_user = 0"""
    )
    fun getChatPreviews(): Flow<List<ChatPreview>>
}