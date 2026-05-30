package com.example.onlinecoursesclient.domain.usecase

import com.example.onlinecoursesclient.domain.repository.CourseRepository

class EnrollCurrentUserUseCase(
    private val courseRepository: CourseRepository
) {

    suspend operator fun invoke(
        courseId: Int,
        teacherId: Int
    ) {
        courseRepository.enrollCurrentUser(
            courseId = courseId,
            teacherId = teacherId
        )
    }
}