package com.example.onlinecoursesclient.domain.repository

import com.example.onlinecoursesclient.domain.model.Course
import com.example.onlinecoursesclient.domain.model.Enrollment
import com.example.onlinecoursesclient.domain.model.Teacher

interface CourseRepository {
    suspend fun getCourses(): List<Course>
    suspend fun getTeachersByCourseId(courseId: Int): List<Teacher>


    suspend fun enrollCurrentUser(
        courseId: Int,
        teacherId: Int
    )

    suspend fun cancelCurrentUserEnrollment(courseId: Int)
    suspend fun getCurrentUserEnrollments(): List<Enrollment>
    suspend fun getCurrentUserCourses(): List<Course>
}