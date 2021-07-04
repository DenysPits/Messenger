package com.example.messenger.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.messenger.data.entity.User
import com.example.messenger.data.repository.UserRepository
import kotlinx.coroutines.runBlocking

class RegistrationViewModel(private val repository: UserRepository) : ViewModel() {
    lateinit var avatarUri: Uri

    suspend fun saveUser(name: String, tag: String, avatar: String) {
        if (validateUserInput(name, tag)) {
            repository.save(User(name = name, tag = tag, avatar = avatar))
        } else throw IllegalArgumentException("Invalid input. Make sure that fields are not empty or long length")
    }

    fun isUsersTableEmpty(): Boolean = runBlocking {
        repository.isTableEmpty()
    }

    private fun validateUserInput(name: String, tag: String): Boolean {
        return !(name.isBlank() || tag.isBlank() || name.length > 30 || tag.length > 30)
    }

    fun isAvatarSelected() = ::avatarUri.isInitialized
}

class RegistrationViewModelFactory(private val repository: UserRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistrationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}