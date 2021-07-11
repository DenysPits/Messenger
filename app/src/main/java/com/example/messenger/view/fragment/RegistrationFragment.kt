package com.example.messenger.view.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.messenger.MessengerApplication
import com.example.messenger.R
import com.example.messenger.databinding.RegistrationFragmentBinding
import com.example.messenger.model.repository.FailStatusException
import com.example.messenger.model.repository.TagIsTakenException
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
        setHasOptionsMenu(true)
        if (!viewModel.isUsersTableEmpty()) {
            val action = RegistrationFragmentDirections.actionRegistrationFragmentToChatPreviewsFragment()
            findNavController().navigate(action)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.edit_profile).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
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
                    Toast.makeText(context, getString(R.string.tag_is_occupied), Toast.LENGTH_SHORT).show()
                } catch (e: FailStatusException) {
                    Toast.makeText(context, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
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