package com.example.messenger.view.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.example.messenger.MessengerApplication
import com.example.messenger.databinding.ChatPreviewsFragmentBinding
import com.example.messenger.model.repository.UserNotFoundException
import com.example.messenger.view.adapter.ChatPreviewsAdapter
import com.example.messenger.viewmodel.ChatPreviewsViewModel
import com.example.messenger.viewmodel.ChatPreviewsViewModelFactory
import kotlinx.coroutines.launch

class ChatPreviewsFragment : Fragment() {

    private lateinit var binding: ChatPreviewsFragmentBinding
    private val viewModel: ChatPreviewsViewModel by viewModels {
        val application = activity?.application as MessengerApplication
        ChatPreviewsViewModelFactory(application.userRepository, application.messageRepository)
    }
    private lateinit var fabMainAnimator: ObjectAnimator
    private lateinit var alphaPanelAnimator: ObjectAnimator
    private lateinit var fabRotateAnimator: ObjectAnimator

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
        viewModel.chatPreviews.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        var areUsersReady = false
        var areMessagesReady = false

        fun restorePreviewsIfReady() {
            if (areMessagesReady && areUsersReady) {
                viewModel.restorePreviews()
            }
        }

        viewModel.users.observe(viewLifecycleOwner) {
            areUsersReady = true
            restorePreviewsIfReady()
        }
        viewModel.messages.observe(viewLifecycleOwner) {
            areMessagesReady = true
            restorePreviewsIfReady()
        }

        initAnimators()
        addTextChangeListener()
        binding.floatingActionButton.setOnClickListener {
            animateFindUserPanel()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.users.value = null
        viewModel.messages.value = null
    }

    private fun initAnimators() {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0.73f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0.73f)
        val elevation = PropertyValuesHolder.ofFloat("elevation", 6f, 0f)
        fabMainAnimator = ObjectAnimator.ofPropertyValuesHolder(binding.floatingActionButton, scaleX, scaleY, elevation)
        alphaPanelAnimator = ObjectAnimator.ofFloat(binding.findUserPanel, View.ALPHA, 0f, 1f)
        alphaPanelAnimator.duration = 400
        fabRotateAnimator = ObjectAnimator.ofFloat(binding.floatingActionButton, View.ROTATION, 0f, -45f)
    }

    private fun addTextChangeListener() {
        val editText = binding.enteredTag
        editText.addTextChangedListener (object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()
                if (before == 0 && text.isNotEmpty()) {
                    fabRotateAnimator.reverse()
                } else if (before > 0 && text.isEmpty() && binding.findUserPanel.isVisible) {
                    fabRotateAnimator.start()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun animateFindUserPanel() {
        val findUserPanel = binding.findUserPanel
        val editText = binding.enteredTag
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(fabMainAnimator, alphaPanelAnimator)
        var isReverse = false
        alphaPanelAnimator.doOnEnd {
            if (isReverse) {
                findUserPanel.visibility = View.GONE
                binding.enteredTag.text.clear()
            }
        }
        if (!findUserPanel.isVisible) {
            isReverse = false
            findUserPanel.visibility = View.VISIBLE
            if (editText.text.isEmpty()) {
                fabRotateAnimator.start()
            }
            animatorSet.start()
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
                    viewModel.addNewChat(binding.enteredTag.text.toString())
                }
            } catch (e: UserNotFoundException) {
                Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}