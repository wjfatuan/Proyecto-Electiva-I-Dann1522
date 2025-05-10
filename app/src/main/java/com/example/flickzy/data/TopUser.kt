package com.example.flickzy.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopUser(
    @SerialName("my_id")
    val myId : Int,
    @SerialName("chat_id")
    val chatId: Int
)

@Serializable
data class TopUserResponse(
    @SerialName("other_user_name")
    val otherUserName: String,
    @SerialName("online_status")
    val onlineStatus : Int
)
