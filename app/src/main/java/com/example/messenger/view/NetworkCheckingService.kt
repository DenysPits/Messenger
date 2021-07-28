package com.example.messenger.view

import android.content.Intent
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.messenger.MessengerApplication
import com.example.messenger.R
import com.example.messenger.model.entity.Message
import com.example.messenger.model.entity.MessageAction
import com.example.messenger.model.entity.changeBase64ToPath
import com.example.messenger.model.repository.MessageRepository
import com.example.messenger.model.repository.UserRepository
import com.example.messenger.utils.NotificationHandler
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import java.net.SocketTimeoutException

class NetworkCheckingService : LifecycleService() {
    private lateinit var job: Job
    private lateinit var userRepository: UserRepository
    private lateinit var messageRepository: MessageRepository

    companion object {
        var isReadyToSendNotifications = false
        var forbiddenIdToSend: Long? = null
    }

    override fun onCreate() {
        super.onCreate()
        userRepository = (application as MessengerApplication).userRepository
        messageRepository = (application as MessengerApplication).messageRepository
        NotificationHandler.createChannel(
            getString(R.string.message_notification_channel_id),
            getString(R.string.message_notification_channel_name)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        checkNewMessages()
        return START_STICKY
    }

    override fun onDestroy() {
        stopCheckingMessages()
        super.onDestroy()
    }

    private fun checkNewMessages() {
        job = lifecycleScope.launch(Dispatchers.IO) {
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

    private fun stopCheckingMessages() {
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
                val companionId = message.companionId
                if (isReadyToSendNotifications && companionId != forbiddenIdToSend) {
                    sendNotification(companionId, message)
                }
                messageRepository.saveLocally(message)
                if (!userRepository.isUserInDatabase(companionId)) {
                    companionIds.add(companionId)
                }
            } else if (message.action.name.equals(MessageAction.UPDATE.name, true)) {
                companionIds.add(message.text.toLong())
            }
        }
        return companionIds
    }

    private suspend fun sendNotification(
        companionId: Long,
        message: Message
    ) {
        val user = userRepository.getUser(companionId)
        withContext(Dispatchers.Main) {
            user.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { user ->
                if (user != null) {
                    NotificationHandler.sendNotification(
                        companionId,
                        user.name,
                        message.text,
                        user.avatar
                    )
                }
            }
        }
    }

    private suspend fun addNewUser(id: Long) {
        val user = userRepository.getById(id)
        user.changeBase64ToPath()
        userRepository.saveLocally(user)
    }
}