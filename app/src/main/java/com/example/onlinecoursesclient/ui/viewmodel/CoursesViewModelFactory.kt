package com.example.onlinecoursesclient.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.onlinecoursesclient.domain.repository.CourseRepository
import com.example.onlinecoursesclient.domain.usecase.*

class CoursesViewModelFactory(
    private val courseRepository: CourseRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CoursesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CoursesViewModel(
                getCoursesUseCase = GetCoursesUseCase(courseRepository),
                getTeachersByCourseUseCase = GetTeachersByCourseUseCase(courseRepository),
                enrollCurrentUserUseCase = EnrollCurrentUserUseCase(courseRepository),
                cancelCurrentUserEnrollmentUseCase = CancelCurrentUserEnrollmentUseCase(courseRepository),
                getCurrentUserEnrollmentsUseCase = GetCurrentUserEnrollmentsUseCase(courseRepository),
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}