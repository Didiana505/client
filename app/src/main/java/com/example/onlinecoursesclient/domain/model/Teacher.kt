package com.example.onlinecoursesclient.domain.model

data class Teacher(
    val id: Int,
    val fullName: String,
    val position: String,
    val courseId: Int
)