package com.example.messenger.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.messenger.MessengerApplication
import com.example.messenger.databinding.ChatFragmentBinding
import com.example.messenger.view.adapter.ChatPreviewsAdapter
import com.example.messenger.viewmodel.ChatViewModel
import com.example.messenger.viewmodel.ChatViewModelFactory

class ChatFragment : Fragment() {

    private lateinit var binding: ChatFragmentBinding
    private val viewModel: ChatViewModel by viewModels {
        val application = activity?.application as MessengerApplication
        ChatViewModelFactory(application.userRepository, application.messageRepository)
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
        val adapter = ChatPreviewsAdapter()
        binding.recyclerView.adapter = adapter
    }
}