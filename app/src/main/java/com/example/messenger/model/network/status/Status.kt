package com.example.messenger.model.network.status

import com.google.gson.annotations.SerializedName

enum class Status {
    @SerializedName("success") SUCCESS,
    @SerializedName("fail") FAIL,
    @SerializedName("tagIsTaken") TAG_IS_TAKEN
}