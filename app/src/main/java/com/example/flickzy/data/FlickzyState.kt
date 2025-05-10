package com.example.flickzy.data

data class FlickzyState(
    val loggedUserId: Int = -1,
    val loggedUserName: String = "",
    val loggedUserMail: String = "",
    val onlineJWT: String = "",
    val selectedChatId: Int = -1
)
