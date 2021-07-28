package com.example.messenger.model.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.messenger.model.entity.Message
import io.reactivex.rxjava3.core.Observable

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages WHERE companion_id=:companionId")
    fun getMessagesWithCompanion(companionId: Long): Observable<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(message: Message)
}