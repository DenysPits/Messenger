package com.example.messenger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.messenger.model.repository.MessageRepository
import com.example.messenger.model.repository.UserRepository

class ChatViewModel(
    private val userRepository: UserRepository,
    messageRepository: MessageRepository
) : ViewModel() {

}

class ChatViewModelFactory(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(userRepository, messageRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}