package com.example.messenger.model.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.messenger.model.entity.User
import io.reactivex.rxjava3.core.Observable

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(user: User)

    @Query("SELECT COUNT(*) FROM users WHERE id=:id")
    suspend fun isUserInDatabase(id: Long): Int

    @Query("SELECT COUNT(*) FROM users")
    suspend fun countRows(): Int

    @Query("SELECT id FROM users WHERE is_my_user=1 LIMIT 1")
    suspend fun getMyId(): Long

    @Query("SELECT * FROM users WHERE is_my_user=1 LIMIT 1")
    fun getMyUser(): Observable<User>

    @Query("SELECT * FROM users WHERE id=:id")
    fun getUser(id: Long): Observable<User>
}