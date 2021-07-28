package com.example.messenger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.messenger.model.entity.Message
import com.example.messenger.model.entity.MessageAction
import com.example.messenger.model.repository.MessageRepository
import com.example.messenger.model.repository.UserRepository

class ChatViewModel(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    companionId: Long
) : ViewModel() {

    val messages =
        messageRepository.getMessagesWithCompanion(companionId)
    val companion = userRepository.getUser(companionId)

    suspend fun sendMessage(companionId: Long, text: String) {
        val myId = userRepository.getMyId()
        val message = Message(companionId, myId, true, text, MessageAction.TEXT)
        messageRepository.save(message)
    }
}

class ChatViewModelFactory(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val companionId: Long
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(userRepository, messageRepository, companionId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}