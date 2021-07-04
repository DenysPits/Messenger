package com.example.messenger.model.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.messenger.model.entity.User

@Dao
interface UserDao {
    @Insert
    suspend fun save(user: User)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun countRows(): Int
}