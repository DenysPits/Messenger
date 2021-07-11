package com.example.messenger.model.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.messenger.model.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun save(user: User)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun countRows(): Int

    @Query("SELECT * FROM users WHERE is_my_user = 0")
    fun getUsers(): Flow<List<User>>
}