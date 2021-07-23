package com.example.messenger.model.network.status

import com.google.gson.annotations.Expose

data class StatusResponse(
    @Expose val status: Status,
    @Expose val id: Long,
    @Expose val time: Long = 0
)
