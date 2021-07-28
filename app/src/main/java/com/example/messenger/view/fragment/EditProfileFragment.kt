package com.example.messenger.view.fragment

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.example.messenger.MessengerApplication
import com.example.messenger.R
import com.example.messenger.utils.ImageHandler
import com.example.messenger.viewmodel.EditProfileViewModel
import com.example.messenger.viewmodel.EditProfileViewModelFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileFragment : ProfileFragment() {
    override val viewModel: EditProfileViewModel by viewModels {
        EditProfileViewModelFactory((activity?.application as MessengerApplication).userRepository)
    }
    override val processIntentResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val avatarUri = result.data?.data!!
                viewModel.avatarUri = avatarUri.toString()
                binding.avatar.setImageURI(avatarUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            nameEditText.doOnTextChanged { text, _, _, _ ->
                viewModel.name = text.toString()
            }
            tagEditText.doOnTextChanged { text, _, _, _ ->
                viewModel.tag = text.toString()
            }
            viewModel.user.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe { user ->
                    if (viewModel.isNameSelected()) {
                        nameEditText.setText(viewModel.name)
                    } else {
                        nameEditText.setText(user.name)
                    }
                    if (viewModel.isTagSelected()) {
                        tagEditText.setText(viewModel.tag)
                    } else {
                        tagEditText.setText(user.tag)
                    }
                    if (viewModel.isAvatarSelected()) {
                        avatar.setImageURI(viewModel.avatarUri.toUri())
                    } else if (user.avatar.isNotBlank()) {
                        viewModel.viewModelScope.launch {
                            val bitmap: Bitmap
                            withContext(Dispatchers.IO) {
                                bitmap = ImageHandler.loadBitmapFromStorage(user.avatar)
                            }
                            avatar.setImageBitmap(bitmap)
                        }
                    }
                }
            whatToDo.text = getString(R.string.what_you_can_do)
            actionButton.text = getString(R.string.save_changes)
            setClickListeners()
        }
    }

    override suspend fun performButtonOperation(name: String, tag: String, avatar: Bitmap?) {
        withContext(Dispatchers.IO) {
            val avatarString = ImageHandler.convertBitmapToBase64(avatar)
            viewModel.updateUser(name, tag, avatarString)
        }
        Toast.makeText(
            context,
            getString(R.string.profile_updated),
            Toast.LENGTH_SHORT
        ).show()
    }
}