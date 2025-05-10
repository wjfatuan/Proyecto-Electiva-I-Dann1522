package com.example.flickzy.data

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val mail: String,
    val pass: String
)

@Serializable
data class LoginResponse(
    val message: String? = null,
    val id: Int? = null,
    val username: String? = null,
    val mail: String? = null,
    val online_token: String? = null,
    val persistent_token: String? = null,
    val errors: List<String>? = null
)
