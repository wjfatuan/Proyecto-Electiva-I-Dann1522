package com.example.flickzy.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Chat (
    @SerialName("id_chat")
    val id: Int,
    @SerialName("last_message_text")
    val lastMessageText: String?,
    @SerialName("last_message_time")
    val lastMessageTime : String?,
    @SerialName("other_user_name")
    val otherUserName : String
)

@Serializable
data class ChatRequest(
    @SerialName("my_id")
    val userId: Int
)

@Serializable
data class NewChatRequest(
    @SerialName("my_id")
    val userId: Int,
    @SerialName("dest_id")
    val destId: Int
)

@Serializable
data class Success(
    val message: String
)

@Serializable
data class Error(
    val errors: List<String>
)