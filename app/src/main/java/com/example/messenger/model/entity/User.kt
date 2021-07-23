package com.example.messenger.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.messenger.utils.ImageHandler
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users", indices = [Index(value = ["tag"], unique = true)])
data class User(
    @PrimaryKey @Expose(serialize = false) @SerializedName("id") var id: Long = 0,
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("tag") val tag: String,
    @Expose @SerializedName("photo") var avatar: String,
    @ColumnInfo(name = "is_my_user") @SerializedName("isMyUser") val isMyUser: Boolean = false
)

fun User.changeBase64ToPath() {
    if (avatar.isNotEmpty()) {
        val bitmap = ImageHandler.convertBase64ToBitmap(avatar)
        avatar = ImageHandler.saveImageToInternalStorage(
            bitmap,
            id.toString()
        ) ?: ""
    }
}