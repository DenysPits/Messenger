package com.example.messenger.model.entity

import com.google.gson.annotations.SerializedName

enum class MessageAction {
    @SerializedName("text")
    TEXT,

    @SerializedName("update")
    UPDATE;
}