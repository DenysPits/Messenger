package com.example.messenger.view.fragment

import android.app.Activity.RESULT_OK
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messenger.MessengerApplication
import com.example.messenger.R
import com.example.messenger.utils.ImageHandler
import com.example.messenger.viewmodel.RegistrationViewModel
import com.example.messenger.viewmodel.RegistrationViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegistrationFragment : ProfileFragment() {
    override val viewModel: RegistrationViewModel by viewModels {
        RegistrationViewModelFactory((activity?.application as MessengerApplication).userRepository)
    }
    override val processIntentResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val avatarUri = result.data?.data!!
                viewModel.avatar = avatarUri.toString()
                binding.avatar.setImageURI(avatarUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (!viewModel.isUsersTableEmpty()) {
            val action =
                RegistrationFragmentDirections.actionRegistrationFragmentToChatPreviewsFragment()
            findNavController().navigate(action)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            if (viewModel.isAvatarSelected()) {
                avatar.setImageURI(viewModel.avatar.toUri())
            }
            whatToDo.text = getString(R.string.what_to_do)
            actionButton.text = getString(R.string.sign_up)
        }
        setClickListeners()
    }


    override suspend fun performButtonOperation(name: String, tag: String, avatar: Bitmap?) {
        withContext(Dispatchers.IO) {
            val avatarString = ImageHandler.convertBitmapToBase64(avatar)
            viewModel.saveUser(name, tag, avatarString)
        }
        val action =
            RegistrationFragmentDirections.actionRegistrationFragmentToChatPreviewsFragment()
        findNavController().navigate(action)
    }
}