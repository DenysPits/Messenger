package com.example.messenger.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.messenger.MessengerApplication
import com.example.messenger.data.repository.FailStatusException
import com.example.messenger.data.repository.TagIsTakenException
import com.example.messenger.databinding.RegistrationFragmentBinding
import com.example.messenger.utils.Coder
import com.example.messenger.viewmodel.RegistrationViewModel
import com.example.messenger.viewmodel.RegistrationViewModelFactory
import kotlinx.coroutines.launch

class RegistrationFragment : Fragment() {
    private val viewModel: RegistrationViewModel by viewModels {
        RegistrationViewModelFactory((activity?.application as MessengerApplication).userRepository)
    }
    private lateinit var binding: RegistrationFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!viewModel.isUsersTableEmpty()) {
            val action = RegistrationFragmentDirections.actionRegistrationFragmentToChatPreviewsFragment()
            findNavController().navigate(action)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RegistrationFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.isAvatarSelected()) {
            binding.avatar.setImageURI(viewModel.avatarUri)
        }
        binding.avatar.setOnClickListener {
            launchIntent()
        }
        binding.signUpButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val tag = binding.tagEditText.text.toString()
            val avatar = binding.avatar
            val bitmap = (avatar.drawable as? BitmapDrawable)?.bitmap
            val avatarString = Coder.convertBitmapToBase64(bitmap)

            viewModel.viewModelScope.launch() {
                try {
                    viewModel.saveUser(name, tag, avatarString)
                    val action = RegistrationFragmentDirections.actionRegistrationFragmentToChatPreviewsFragment()
                    findNavController().navigate(action)
                } catch (e: TagIsTakenException) {
                    Toast.makeText(context, "This tag is occupied by another user. Come up with another one", Toast.LENGTH_SHORT).show()
                } catch (e: FailStatusException) {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                } catch (e: IllegalArgumentException) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val processIntentResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val avatarUri = result.data?.data!!
                viewModel.avatarUri = avatarUri
                binding.avatar.setImageURI(avatarUri)
            }
        }

    private fun launchIntent() {
        processIntentResult.launch(
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            )
        )
    }
}