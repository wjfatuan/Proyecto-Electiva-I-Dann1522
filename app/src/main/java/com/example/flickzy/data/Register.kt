package com.example.flickzy.data

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val name: String,
    val mail: String,
    val pass: String
)

@Serializable
data class RegisterResponse(
    val message: String? = null,
    val errors: List<String>? = null
)
