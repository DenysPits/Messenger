package com.example.messenger.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.getColorOrThrow
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import com.example.messenger.databinding.MessageItemBinding
import com.example.messenger.model.entity.Message
import java.text.SimpleDateFormat
import java.util.*


class MessageAdapter :
    ListAdapter<Message, MessageAdapter.MessageViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            MessageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MessageViewHolder(
        private var binding: MessageItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceType")
        fun bind(message: Message) {
            binding.apply {
                text.text = message.text
                val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                time.text = simpleDateFormat.format(Date(message.time))
                val typedArray = binding.root.context.theme.obtainStyledAttributes(
                    intArrayOf(
                        R.attr.white,
                        R.attr.myMessageTimeColor,
                        R.attr.plainTextColor,
                        R.attr.companionMessageTimeColor
                    )
                )
                if (message.sentByMe) {
                    text.setTextColor(typedArray.getColorOrThrow(0))
                    time.setTextColor(typedArray.getColorOrThrow(1))
                    setBias(0.99f)
                    messageItem.background =
                        AppCompatResources.getDrawable(root.context, R.drawable.my_message_shape)
                } else {
                    text.setTextColor(typedArray.getColorOrThrow(2))
                    time.setTextColor(typedArray.getColorOrThrow(3))
                    setBias(0.01f)
                    messageItem.background =
                        AppCompatResources.getDrawable(
                            root.context,
                            R.drawable.companion_message_shape
                        )
                }
                typedArray.recycle()
            }
        }

        private fun setBias(bias: Float) {
            val set = ConstraintSet()
            set.clone(binding.root)
            set.setHorizontalBias(R.id.message_item, bias)
            set.applyTo(binding.root)
        }
    }
}