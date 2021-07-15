package com.example.messenger.model.entity

import androidx.room.ColumnInfo

data class ChatPreview(
    @ColumnInfo(name = "user_id") val userId: Long,
    val name: String,
    val message: String?,
    val time: Long?,
    val avatar: String
)