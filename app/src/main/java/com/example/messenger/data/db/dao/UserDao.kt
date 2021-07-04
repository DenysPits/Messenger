package com.example.messenger.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.messenger.data.entity.User

@Dao
interface UserDao {
    @Insert
    suspend fun save(user: User)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun countRows(): Int
}