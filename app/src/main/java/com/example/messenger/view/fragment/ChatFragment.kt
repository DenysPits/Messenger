package com.example.messenger.view.fragment

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.example.messenger.MessengerApplication
import com.example.messenger.R
import com.example.messenger.databinding.ChatFragmentBinding
import com.example.messenger.databinding.ChatToolbarBinding
import com.example.messenger.utils.ImageHandler
import com.example.messenger.utils.NotificationHandler
import com.example.messenger.view.NetworkCheckingService
import com.example.messenger.view.adapter.MessageAdapter
import com.example.messenger.viewmodel.ChatViewModel
import com.example.messenger.viewmodel.ChatViewModelFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ChatFragment : Fragment() {

    private lateinit var binding: ChatFragmentBinding
    private lateinit var toolbarBinding: ChatToolbarBinding
    private val navigationArgs: ChatFragmentArgs by navArgs()
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
        viewModel.messages.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { messages ->
            adapter.submitList(messages) {
                recyclerView.scrollToPosition(messages.size - 1)
            }
        }
        viewModel.companion.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { companion ->
            toolbarBinding.name.text = companion.name
            if (companion.avatar.isEmpty()) {
                val firstLetterOfName = companion.name.take(1).uppercase(Locale.getDefault())
                toolbarBinding.avatarLetter.text = firstLetterOfName
            } else {
                viewModel.viewModelScope.launch {
                    val bitmap: Bitmap
                    withContext(Dispatchers.IO) {
                        bitmap = ImageHandler.loadBitmapFromStorage(companion.avatar)
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

    override fun onStart() {
        super.onStart()
        NotificationHandler.cancelNotification(navigationArgs.companionId)
    }

    override fun onResume() {
        super.onResume()
        NetworkCheckingService.forbiddenIdToSend = navigationArgs.companionId
    }

    override fun onPause() {
        super.onPause()
        NetworkCheckingService.forbiddenIdToSend = null
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

        binding.messageEditText.addTextChangedListener(object : TextWatcher {
            var beforeLength: Int = -1

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                beforeLength = s.toString().length
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textString = s.toString()
                if (beforeLength == 0 && textString.isNotEmpty()) {
                    isEditTextEmpty = false
                    buttonDisappearAnimator.start()
                } else if (beforeLength > 0 && textString.isEmpty()) {
                    isEditTextEmpty = true
                    buttonDisappearAnimator.start()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }
}