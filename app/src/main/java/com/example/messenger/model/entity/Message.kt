package com.example.messenger.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "messages")
data class Message(
    @Expose @PrimaryKey val id: Long,
    @Expose @ColumnInfo(name = "companion_id") val companionId: Long,
    @Expose @ColumnInfo(name = "sent_by_me") val sentByMe: Boolean,
    @Expose val text: String,
    @Expose val time: Long
)