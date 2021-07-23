package com.example.messenger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.messenger.model.entity.User
import com.example.messenger.model.repository.UserRepository
import kotlinx.coroutines.runBlocking

class RegistrationViewModel(private val repository: UserRepository) : ViewModel() {
    lateinit var avatar: String

    suspend fun saveUser(name: String, tag: String, avatar: String) {
        if (validateUserInput(name, tag)) {
            repository.saveGlobally(User(name = name, tag = tag, avatar = avatar, isMyUser = true))
        } else throw IllegalArgumentException("Invalid input. Make sure that fields are not empty or long length")
    }

    fun isUsersTableEmpty(): Boolean = runBlocking {
        repository.isTableEmpty()
    }

    private fun validateUserInput(name: String, tag: String): Boolean {
        return !(name.isBlank() || tag.isBlank() || name.length > 30 || tag.length > 30)
    }

    fun isAvatarSelected() = ::avatar.isInitialized
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