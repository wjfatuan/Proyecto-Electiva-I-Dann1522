package com.example.flickzy.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Int,
    @SerialName("id_chat")
    val idChat: Int,
    @SerialName("id_sender")
    val idSender: Int,
    val content: String,
    val timestamp: String
)

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId : Int,
    @SerialName("sender_id")
    val senderId: Int,
    val content: String
)

@Serializable
data class MessageRequest(
    @SerialName("chat_id")
    val idChat: Int
)