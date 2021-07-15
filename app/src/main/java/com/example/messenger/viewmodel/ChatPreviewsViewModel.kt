package com.example.messenger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.messenger.model.repository.MessageRepository
import com.example.messenger.model.repository.UserRepository

class ChatPreviewsViewModel(
    private val userRepository: UserRepository,
    messageRepository: MessageRepository
) : ViewModel() {

    val chatPreviews = userRepository.getChatPreviews().asLiveData()

    suspend fun addNewChat(tag: String) {
        val user = userRepository.getByTag(tag)
        userRepository.saveLocally(user)
    }
}

class ChatPreviewsViewModelFactory(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatPreviewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatPreviewsViewModel(userRepository, messageRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}