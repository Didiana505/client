package com.example.onlinecoursesclient.domain.usecase

import com.example.onlinecoursesclient.domain.model.Enrollment
import com.example.onlinecoursesclient.domain.repository.CourseRepository

class GetCurrentUserEnrollmentsUseCase(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(): List<Enrollment> {
        return repository.getCurrentUserEnrollments()
    }
}