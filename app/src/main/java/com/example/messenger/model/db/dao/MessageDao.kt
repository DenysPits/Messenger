package com.example.messenger.model.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.messenger.model.entity.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query(
        "SELECT o.* FROM messages o INNER JOIN (SELECT companion_id, MAX(time) AS MaxTime FROM messages GROUP BY companion_id) og ON o.companion_id = og.companion_id AND o.time = og.MaxTime"
    )
    fun getLastMessages(): Flow<List<Message>>
}