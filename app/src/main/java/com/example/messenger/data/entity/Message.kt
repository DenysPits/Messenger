package com.example.messenger.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "companion_id") val companionId: Long,
    @ColumnInfo(name = "sent_by_me") val sentByMe: Boolean,
    val text: String,
    val time: Long
)