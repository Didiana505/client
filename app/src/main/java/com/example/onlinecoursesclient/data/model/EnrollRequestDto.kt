package com.example.onlinecoursesclient.data.model

import kotlinx.serialization.Serializable

@Serializable
data class EnrollRequestDto(
    val firebaseUid: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val courseId: Int,
    val teacherId: Int
)