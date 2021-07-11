package com.example.messenger.model.entity

data class ChatPreview(
    val userId: Long,
    val name: String,
    val message: String,
    val time: Long,
    val avatar: String
)