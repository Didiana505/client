package com.example.onlinecoursesclient.domain.model

data class Enrollment(
    val id: Int,
    val userId: Int,
    val course: Course,
    val teacher: Teacher?,
    val enrolledAt: String
)