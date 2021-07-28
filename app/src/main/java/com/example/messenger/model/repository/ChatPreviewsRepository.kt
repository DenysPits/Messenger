package com.example.messenger.model.repository

import com.example.messenger.model.db.dao.ChatPreviewsDao
import com.example.messenger.model.entity.ChatPreview
import io.reactivex.rxjava3.core.Observable

class ChatPreviewsRepository(private val chatPreviewsDao: ChatPreviewsDao) {

    fun getChatPreviews(): Observable<List<ChatPreview>> {
        return chatPreviewsDao.getChatPreviews()
    }
}