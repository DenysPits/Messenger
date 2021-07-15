package com.example.messenger.view.fragment

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.messenger.MessengerApplication
import com.example.messenger.R
import com.example.messenger.databinding.ChatFragmentBinding
import com.example.messenger.view.adapter.MessageAdapter
import com.example.messenger.viewmodel.ChatViewModel
import com.example.messenger.viewmodel.ChatViewModelFactory

class ChatFragment : Fragment() {

    private lateinit var binding: ChatFragmentBinding
    private val viewModel: ChatViewModel by viewModels {
        val application = activity?.application as MessengerApplication
        ChatViewModelFactory(
            application.userRepository, application.messageRepository,
            requireArguments().getLong("companionId")
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = MessageAdapter()
        binding.recyclerView.adapter = adapter
        viewModel.messages.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        addButtonAnimation()
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
}