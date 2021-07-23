package com.example.messenger.model.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MessageForSerialization(
    @Expose(deserialize = false) @SerializedName("toId") val companionId: Long,
    @Expose(deserialize = false) @SerializedName("fromId") val myId: Long,
    @Expose(deserialize = false) @SerializedName("body") val text: String,
    @Expose(deserialize = false) @SerializedName("action") val action: MessageAction
)