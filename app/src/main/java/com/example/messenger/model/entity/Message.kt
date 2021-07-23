package com.example.messenger.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "messages")
data class Message(
    @Expose(serialize = false) @SerializedName("fromId") @ColumnInfo(name = "companion_id") var companionId: Long,
    @Expose(serialize = false) @SerializedName("toId") @Ignore val myId: Long,
    @ColumnInfo(name = "sent_by_me") var sentByMe: Boolean,
    @Expose @SerializedName("body") var text: String,
    @Expose @Ignore val action: MessageAction
) {
    @Expose(serialize = false)
    @PrimaryKey
    var id: Long? = null

    @Expose(serialize = false)
    var time: Long? = null

    constructor() : this(
        -1,
        -1,
        false,
        "",
        MessageAction.TEXT
    )
}

fun Message.toMessageForSerialization(): MessageForSerialization {
    return MessageForSerialization(companionId, myId, text, action)
}