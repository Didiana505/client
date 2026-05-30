package com.example.onlinecoursesclient.domain.usecase

import com.example.onlinecoursesclient.domain.repository.CourseRepository

class CancelCurrentUserEnrollmentUseCase(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(courseId: Int) {
        repository.cancelCurrentUserEnrollment(courseId)
    }
}