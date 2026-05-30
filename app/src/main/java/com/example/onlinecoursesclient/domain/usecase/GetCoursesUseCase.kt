package com.example.onlinecoursesclient.domain.usecase

import com.example.onlinecoursesclient.domain.model.Course
import com.example.onlinecoursesclient.domain.repository.CourseRepository

class GetCoursesUseCase(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(): List<Course> {
        return repository.getCourses()
    }
}