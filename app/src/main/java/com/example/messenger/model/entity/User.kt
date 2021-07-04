package com.example.messenger.model.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users", indices = [Index(value = ["tag"], unique = true)])
data class User(
    @PrimaryKey @Expose(serialize = false) @SerializedName("id") var id: Long = 0,
    @SerializedName("name") val name: String,
    @SerializedName("tag") val tag: String,
    @SerializedName("photo") val avatar: String
)