package com.example.onlinecoursesclient.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CourseDto(
    val id: Int,
    val title: String,
    val description: String,
    val duration: String,
    val category: String
)