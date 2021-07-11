package com.example.messenger.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users", indices = [Index(value = ["tag"], unique = true)])
data class User(
    @PrimaryKey @Expose(serialize = false) @SerializedName("id") var id: Long = 0,
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("tag") val tag: String,
    @Expose @SerializedName("photo") val avatar: String,
    @ColumnInfo(name = "is_my_user") @SerializedName("isMyUser") val isMyUser: Boolean = false
)