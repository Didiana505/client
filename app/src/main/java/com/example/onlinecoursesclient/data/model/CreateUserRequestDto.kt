package com.example.onlinecoursesclient.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequestDto(
    val email: String,
    val firstName: String,
    val lastName: String
)