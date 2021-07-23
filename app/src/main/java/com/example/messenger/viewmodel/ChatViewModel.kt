package com.example.messenger.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.messenger.model.entity.Message
import com.example.messenger.model.entity.MessageAction
import com.example.messenger.model.repository.MessageRepository
import com.example.messenger.model.repository.UserRepository

class ChatViewModel(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    companionId: Long
) : ViewModel() {

    val messages: LiveData<List<Message>> =
        messageRepository.getMessagesWithCompanion(companionId).asLiveData()
    val companionName: LiveData<String> = userRepository.getUserName(companionId).asLiveData()
    val companionAvatar: LiveData<String> = userRepository.getUserAvatar(companionId).asLiveData()

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