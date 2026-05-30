package com.example.onlinecoursesclient.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val firebaseUid: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val age: Int? = null
)