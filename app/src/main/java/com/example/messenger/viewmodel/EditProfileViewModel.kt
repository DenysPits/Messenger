package com.example.messenger.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.messenger.model.entity.User
import com.example.messenger.model.repository.UserRepository

class EditProfileViewModel(private val repository: UserRepository) : ViewModel() {

    lateinit var name: String
    lateinit var tag: String
    lateinit var avatarUri: String
    val user: LiveData<User> = repository.getMyUser().asLiveData()

    suspend fun updateUser(name: String, tag: String, avatar: String) {
        if (validateUserInput(name, tag)) {
            repository.updateGlobally(User(getMyId(), name, tag, avatar, true))
        } else throw IllegalArgumentException("Invalid input. Make sure that fields are not empty or long length")
    }

    fun isAvatarSelected() = ::avatarUri.isInitialized
    fun isNameSelected() = ::name.isInitialized
    fun isTagSelected() = ::tag.isInitialized

    private suspend fun getMyId(): Long {
        return repository.getMyId()
    }

    private fun validateUserInput(name: String, tag: String): Boolean {
        return !(name.isBlank() || tag.isBlank() || name.length > 30 || tag.length > 30)
    }
}

class EditProfileViewModelFactory(private val repository: UserRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}