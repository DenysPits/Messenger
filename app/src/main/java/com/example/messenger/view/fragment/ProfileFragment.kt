package com.example.messenger.view.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.R
import com.example.messenger.databinding.ProfileFragmentBinding
import com.example.messenger.model.repository.FailStatusException
import com.example.messenger.model.repository.TagIsTakenException
import kotlinx.coroutines.launch


abstract class ProfileFragment : Fragment() {
    protected lateinit var binding: ProfileFragmentBinding
    protected abstract val viewModel: ViewModel
    protected abstract val processIntentResult: ActivityResultLauncher<Intent>

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.edit_profile).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ProfileFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    protected fun setClickListeners() {
        binding.avatar.setOnClickListener {
            launchIntent()
        }
        binding.actionButton.setOnClickListener {
            viewModel.viewModelScope.launch {
                binding.profileContent.visibility = View.GONE
                binding.loadingWindow.root.visibility = View.VISIBLE
                val name = binding.nameEditText.text.toString()
                val tag = binding.tagEditText.text.toString()
                val avatar = binding.avatar
                val bitmap = (avatar.drawable as? BitmapDrawable)?.bitmap

                val imm: InputMethodManager? =
                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)

                val job = launch {
                    try {
                        performButtonOperation(name, tag, bitmap)
                    } catch (e: TagIsTakenException) {
                        Toast.makeText(
                            context,
                            getString(R.string.tag_is_occupied),
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: FailStatusException) {
                        Toast.makeText(
                            context,
                            getString(R.string.something_went_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: IllegalArgumentException) {
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
                job.join()
                binding.profileContent.visibility = View.VISIBLE
                binding.loadingWindow.root.visibility = View.GONE
            }
        }
    }

    abstract suspend fun performButtonOperation(name: String, tag: String, avatar: Bitmap?)

    private fun launchIntent() {
        processIntentResult.launch(
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            )
        )
    }
}