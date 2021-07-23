package com.example.messenger.model.repository

import com.example.messenger.model.db.dao.ChatPreviewsDao
import com.example.messenger.model.entity.ChatPreview
import kotlinx.coroutines.flow.Flow

class ChatPreviewsRepository(private val chatPreviewsDao: ChatPreviewsDao) {

    fun getChatPreviews(): Flow<List<ChatPreview>> {
        return chatPreviewsDao.getChatPreviews()
    }
}