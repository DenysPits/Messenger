package com.example.messenger.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.messenger.model.entity.changeBase64ToPath
import com.example.messenger.model.repository.ChatPreviewsRepository
import com.example.messenger.model.repository.UserRepository

class ChatPreviewsViewModel(
    private val userRepository: UserRepository,
    chatPreviewsRepository: ChatPreviewsRepository
) : ViewModel() {

    val chatPreviews = chatPreviewsRepository.getChatPreviews().asLiveData()

    suspend fun addNewUser(tag: String) {
        var time1 = System.currentTimeMillis()
        val user = userRepository.getByTag(tag)
        var time2 = System.currentTimeMillis()
        Log.d("Time", "getUserByTag: " + (time2 - time1))
        time1 = System.currentTimeMillis()
        user.changeBase64ToPath()
        time2 = System.currentTimeMillis()
        Log.d("Time", "changeBase64ToPath: " + (time2 - time1))
        time1 = System.currentTimeMillis()
        userRepository.saveLocally(user)
        time2 = System.currentTimeMillis()
        Log.d("Time", "saveLocally: " + (time2 - time1))
    }
}

class ChatPreviewsViewModelFactory(
    private val userRepository: UserRepository,
    private val chatPreviewsRepository: ChatPreviewsRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatPreviewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatPreviewsViewModel(
                userRepository,
                chatPreviewsRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}