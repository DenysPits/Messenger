package com.example.messenger.viewmodel

import androidx.lifecycle.*
import com.example.messenger.model.entity.ChatPreview
import com.example.messenger.model.repository.MessageRepository
import com.example.messenger.model.repository.UserRepository

class ChatPreviewsViewModel(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _chatPreviews = MutableLiveData<MutableList<ChatPreview>>(mutableListOf())
    val chatPreviews: LiveData<MutableList<ChatPreview>>
        get() = _chatPreviews

    val messages = messageRepository.getLastMessages().asLiveData() as MutableLiveData
    val users = userRepository.getUsers().asLiveData() as MutableLiveData

    fun restorePreviews() {
        if (!users.value.isNullOrEmpty() && messages.value != null) {
            for (user in users.value!!) {
                var isNewChat = true
                var chatPreview: ChatPreview? = null
                for (message in messages.value!!) {
                    if (message.companionId == user.id) {
                        chatPreview =
                            ChatPreview(user.id, user.name, message.text, message.time, user.avatar)
                        isNewChat = false
                        break
                    }
                }
                if (isNewChat) {
                    chatPreview = ChatPreview(user.id, user.name, "No messages yet", -1, user.avatar)
                }
                _chatPreviews.value?.add(chatPreview!!)
            }
        }
    }

    suspend fun addNewChat(tag: String) {
        val user = userRepository.getByTag(tag)
        val chatPreview = ChatPreview(user.id, user.name, "No messages yet", -1, user.avatar)
        _chatPreviews.value?.add(chatPreview)
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