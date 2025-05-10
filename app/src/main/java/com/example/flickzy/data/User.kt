package com.example.flickzy.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val mail: String,
    val name: String,
    val online: Int
)
