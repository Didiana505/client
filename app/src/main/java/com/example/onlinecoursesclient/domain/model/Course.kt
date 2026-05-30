package com.example.onlinecoursesclient.domain.model

data class Course(
    val id: Int,
    val title: String,
    val description: String,
    val duration: String,
    val category: String
)