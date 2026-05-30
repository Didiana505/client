package com.example.onlinecoursesclient.data.model

import kotlinx.serialization.Serializable

@Serializable
data class EnrollmentDto(
    val id: Int,
    val userId: Int,
    val course: CourseDto,
    val teacher: TeacherDto? = null,
    val enrolledAt: String
)