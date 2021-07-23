package com.example.messenger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.messenger.model.entity.Message
import com.example.messenger.model.entity.MessageAction
import com.example.messenger.model.entity.changeBase64ToPath
import com.example.messenger.model.repository.MessageRepository
import com.example.messenger.model.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

open class NetworkCheckViewModel(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
) : ViewModel() {

    private lateinit var job: Job

    fun checkNewMessages() {
        job = viewModelScope.launch(Dispatchers.IO) {
            val myId = userRepository.getMyId()
            while (true) {
                try {
                    val messages = messageRepository.pullMessagesOut(myId, true)
                    val companionIds = filterReceivedMessages(messages)
                    for (companionId in companionIds) {
                        addNewUser(companionId)
                    }
                    delay(1000)
                } catch (ignore: SocketTimeoutException) {
                }
            }
        }
    }

    fun stopCheckingMessages() {
        job.cancel()
    }

    /**
     * If message notifies about user's update or is sent from new user, the companion id is added to return set.
     * @param messages messages to filter
     * @return companion ids that should be updated
     */
    private suspend fun filterReceivedMessages(messages: List<Message>): Set<Long> {
        val companionIds = mutableSetOf<Long>()
        for (message in messages) {
            if (message.action.name.equals(MessageAction.TEXT.name, true)) {
                messageRepository.saveLocally(message)
                if (!userRepository.isUserInDatabase(message.companionId)) {
                    companionIds.add(message.companionId)
                }
            } else if (message.action.name.equals(MessageAction.UPDATE.name, true)) {
                companionIds.add(message.text.toLong())
            }
        }
        return companionIds
    }

    private suspend fun addNewUser(id: Long) {
        val user = userRepository.getById(id)
        user.changeBase64ToPath()
        userRepository.saveLocally(user)
    }
}

class NetworkCheckViewModelFactory(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NetworkCheckViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NetworkCheckViewModel(
                userRepository,
                messageRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}