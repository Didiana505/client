package com.example.onlinecoursesclient.domain.usecase

import com.example.onlinecoursesclient.domain.model.Teacher
import com.example.onlinecoursesclient.domain.repository.CourseRepository

class GetTeachersByCourseUseCase(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(courseId: Int): List<Teacher> {
        return repository.getTeachersByCourseId(courseId)
    }
}