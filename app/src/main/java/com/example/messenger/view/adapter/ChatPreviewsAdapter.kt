package com.example.messenger.view.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.databinding.ChatPreviewItemBinding
import com.example.messenger.model.entity.ChatPreview
import com.example.messenger.utils.ImageHandler
import com.example.messenger.view.fragment.ChatPreviewsFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ChatPreviewsAdapter :
    ListAdapter<ChatPreview, ChatPreviewsAdapter.ChatPreviewsViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ChatPreview>() {
            override fun areItemsTheSame(oldItem: ChatPreview, newItem: ChatPreview): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: ChatPreview, newItem: ChatPreview): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ChatPreviewsViewHolder(
            ChatPreviewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ChatPreviewsViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            val action =
                ChatPreviewsFragmentDirections.actionChatPreviewsFragmentToChatFragment(item.userId)
            holder.itemView.findNavController().navigate(action)
        }
        holder.bind(item)
    }

    class ChatPreviewsViewHolder(
        private var binding: ChatPreviewItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chatPreview: ChatPreview) {
            binding.apply {
                userName.text = chatPreview.name
                message.text = chatPreview.message ?: "No messages yet"
                val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val time = chatPreview.time ?: -1
                this.time.text =
                    if (time > 0) simpleDateFormat.format(Date(time)) else ""
                if (chatPreview.avatar.isEmpty()) {
                    avatarLetter.text = chatPreview.name.take(1).uppercase(Locale.getDefault())
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        val bitmap: Bitmap
                        withContext(Dispatchers.IO) {
                            bitmap = ImageHandler.loadBitmapFromStorage(chatPreview.avatar)
                        }
                        avatar.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }
}