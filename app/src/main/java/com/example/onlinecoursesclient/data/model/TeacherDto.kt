package com.example.onlinecoursesclient.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TeacherDto(
    val id: Int,
    val fullName: String,
    val position: String,
    val courseId: Int
)