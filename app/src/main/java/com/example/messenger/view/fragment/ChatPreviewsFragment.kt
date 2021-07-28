package com.example.messenger.view.fragment

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.example.messenger.MessengerApplication
import com.example.messenger.databinding.ChatPreviewsFragmentBinding
import com.example.messenger.model.repository.UserNotFoundException
import com.example.messenger.view.NetworkCheckingService
import com.example.messenger.view.adapter.ChatPreviewsAdapter
import com.example.messenger.viewmodel.ChatPreviewsViewModel
import com.example.messenger.viewmodel.ChatPreviewsViewModelFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatPreviewsFragment : Fragment() {

    private lateinit var binding: ChatPreviewsFragmentBinding
    private val viewModel: ChatPreviewsViewModel by viewModels {
        val application = activity?.application as MessengerApplication
        ChatPreviewsViewModelFactory(
            application.userRepository,
            application.chatPreviewsRepository
        )
    }
    private lateinit var fabMainAnimator: ObjectAnimator
    private lateinit var alphaPanelAnimator: ObjectAnimator
    private lateinit var fabRotateAnimator: ObjectAnimator
    private var isReverse = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatPreviewsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ChatPreviewsAdapter()
        binding.recyclerView.adapter = adapter
        viewModel.chatPreviews.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { chatPreviews ->
            adapter.submitList(chatPreviews.sortedByDescending { chatPreview -> chatPreview.time })
        }
        val intent = Intent(context, NetworkCheckingService::class.java)
        requireActivity().startService(intent)
        initAnimators()
        addTextChangeListener()
        binding.floatingActionButton.setOnClickListener {
            animateFindUserPanel()
        }
    }

    override fun onStart() {
        super.onStart()
        NetworkCheckingService.isReadyToSendNotifications = false
    }

    override fun onStop() {
        super.onStop()
        NetworkCheckingService.isReadyToSendNotifications = true
    }

    @SuppressLint("Recycle")
    private fun initAnimators() {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0.73f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0.73f)
        val elevation = PropertyValuesHolder.ofFloat("elevation", 6f, 0f)
        fabMainAnimator = ObjectAnimator.ofPropertyValuesHolder(
            binding.floatingActionButton,
            scaleX,
            scaleY,
            elevation
        )
        fabRotateAnimator =
            ObjectAnimator.ofFloat(binding.floatingActionButton, View.ROTATION, 0f, -45f)
        alphaPanelAnimator = ObjectAnimator.ofFloat(binding.findUserPanel, View.ALPHA, 0f, 1f)
        alphaPanelAnimator.duration = 400
        alphaPanelAnimator.doOnEnd {
            if (isReverse) {
                binding.findUserPanel.visibility = View.GONE
                binding.enteredTag.text.clear()
            }
        }
    }

    private fun addTextChangeListener() {
        val editText = binding.enteredTag
        editText.doOnTextChanged { text, _, before, _ ->
            val textString = text.toString()
            if (before == 0 && textString.isNotEmpty()) {
                fabRotateAnimator.reverse()
            } else if (before > 0 && textString.isEmpty() && binding.findUserPanel.isVisible) {
                fabRotateAnimator.start()
            }
        }
    }

    private fun animateFindUserPanel() {
        val imm: InputMethodManager? =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)

        val editText = binding.enteredTag
        val findUserPanel = binding.findUserPanel
        if (!findUserPanel.isVisible) {
            isReverse = false
            findUserPanel.visibility = View.VISIBLE
            if (editText.text.isEmpty()) {
                fabRotateAnimator.start()
            }
            alphaPanelAnimator.start()
            fabMainAnimator.start()
        } else {
            isReverse = true
            addNewChat()
            if (editText.text.isEmpty()) {
                fabRotateAnimator.reverse()
            }
            fabMainAnimator.reverse()
            alphaPanelAnimator.reverse()
        }
    }

    private fun addNewChat() {
        viewModel.viewModelScope.launch {
            try {
                if (binding.enteredTag.text.toString().isNotBlank()) {
                    withContext(Dispatchers.IO) {
                        viewModel.addNewUser(binding.enteredTag.text.toString())
                    }
                }
            } catch (e: UserNotFoundException) {
                Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}