package com.example.messenger.view.fragment

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.core.animation.doOnEnd
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.example.messenger.MessengerApplication
import com.example.messenger.R
import com.example.messenger.databinding.ChatFragmentBinding
import com.example.messenger.databinding.ChatToolbarBinding
import com.example.messenger.utils.ImageHandler
import com.example.messenger.view.adapter.MessageAdapter
import com.example.messenger.viewmodel.ChatViewModel
import com.example.messenger.viewmodel.ChatViewModelFactory
import com.example.messenger.viewmodel.NetworkCheckViewModel
import com.example.messenger.viewmodel.NetworkCheckViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ChatFragment : Fragment() {

    private lateinit var binding: ChatFragmentBinding
    private lateinit var toolbarBinding: ChatToolbarBinding
    private val navigationArgs: ChatFragmentArgs by navArgs()
    private val networkCheckViewModel: NetworkCheckViewModel by viewModels {
        val application = activity?.application as MessengerApplication
        NetworkCheckViewModelFactory(
            application.userRepository,
            application.messageRepository,
        )
    }
    private val viewModel: ChatViewModel by viewModels {
        val application = activity?.application as MessengerApplication
        ChatViewModelFactory(
            application.userRepository, application.messageRepository,
            navigationArgs.companionId
        )
    }
    private val isButtonForSending: Boolean
        get() = !binding.messageEditText.text.isNullOrEmpty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.edit_profile).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatFragmentBinding.inflate(inflater, container, false)
        toolbarBinding =
            ChatToolbarBinding.inflate(inflater, requireActivity().findViewById(R.id.toolbar))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = binding.recyclerView
        val adapter = MessageAdapter()
        recyclerView.adapter = adapter
        viewModel.messages.observe(viewLifecycleOwner) {
            adapter.submitList(it) {
                recyclerView.scrollToPosition(it.size - 1)
            }
        }
        viewModel.companionName.observe(viewLifecycleOwner) {
            toolbarBinding.name.text = it
        }
        viewModel.companionAvatar.observe(viewLifecycleOwner) { avatar ->
            if (avatar.isEmpty()) {
                viewModel.companionName.observe(viewLifecycleOwner) {
                    val firstLetterOfName = it.take(1).uppercase(Locale.getDefault())
                    toolbarBinding.avatarLetter.text = firstLetterOfName
                }
            } else {
                viewModel.viewModelScope.launch {
                    val bitmap: Bitmap
                    withContext(Dispatchers.IO) {
                        bitmap = ImageHandler.loadBitmapFromStorage(avatar)
                    }
                    toolbarBinding.avatar.setImageBitmap(bitmap)
                }
            }
        }
        recyclerView.addOnLayoutChangeListener { v, _, _, _, bottom, _, _, _, oldBottom ->
            val dy = oldBottom - bottom
            if (dy > 0) {
                v.scrollBy(0, dy)
            }
        }
        networkCheckViewModel.checkNewMessages()
        addButtonAnimation()
        binding.sendAttachButton.setOnClickListener {
            if (isButtonForSending) {
                val text = binding.messageEditText.text.toString()
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    viewModel.sendMessage(navigationArgs.companionId, text)
                }
                binding.messageEditText.text.clear()
            }
        }
    }


    private fun addButtonAnimation() {
        val button = binding.sendAttachButton
        val buttonDisappearAnimator = ObjectAnimator.ofFloat(button, View.ALPHA, 1f, 0f)
        buttonDisappearAnimator.duration = 100
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 1f)
        val buttonAppearAnimator = ObjectAnimator.ofPropertyValuesHolder(button, scaleX, scaleY)
        buttonAppearAnimator.duration = 150

        var isEditTextEmpty = true
        buttonDisappearAnimator.doOnEnd {
            if (isEditTextEmpty) {
                button.setImageResource(R.drawable.ic_attach_file)
            } else {
                button.setImageResource(R.drawable.ic_send)
            }
            button.alpha = 1f
            buttonAppearAnimator.start()
        }

        binding.messageEditText.doOnTextChanged { text, _, before, _ ->
            val textString = text.toString()
            if (before == 0 && textString.isNotEmpty()) {
                isEditTextEmpty = false
                buttonDisappearAnimator.start()
            } else if (before > 0 && textString.isEmpty()) {
                isEditTextEmpty = true
                buttonDisappearAnimator.start()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        networkCheckViewModel.stopCheckingMessages()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().findViewById<Toolbar>(R.id.toolbar).apply {
            val image = findViewById<View>(R.id.avatar_card)
            val name = findViewById<View>(R.id.name)
            removeView(image)
            removeView(name)
        }
    }
}