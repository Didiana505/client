package com.example.onlinecoursesclient.data.repository

import com.example.onlinecoursesclient.data.model.CourseDto
import com.example.onlinecoursesclient.data.model.EnrollRequestDto
import com.example.onlinecoursesclient.data.model.EnrollmentDto
import com.example.onlinecoursesclient.data.model.TeacherDto
import com.example.onlinecoursesclient.data.remote.httpClient
import com.example.onlinecoursesclient.domain.model.Course
import com.example.onlinecoursesclient.domain.model.Enrollment
import com.example.onlinecoursesclient.domain.model.Teacher
import com.example.onlinecoursesclient.domain.repository.CourseRepository
import com.example.onlinecoursesclient.utils.TokenManager
import com.google.firebase.auth.FirebaseAuth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess

class CourseRepositoryImpl(
    private val client: HttpClient = httpClient
) : CourseRepository {

    private val baseUrl = "http://10.0.2.2:8080"
    private val auth = FirebaseAuth.getInstance()

    override suspend fun getCourses(): List<Course> {
        val response: List<CourseDto> = client
            .get("$baseUrl/courses")
            .body()

        return response.map { dto ->
            dto.toCourse()
        }
    }

    override suspend fun getTeachersByCourseId(courseId: Int): List<Teacher> {
        val response: List<TeacherDto> = client
            .get("$baseUrl/courses/$courseId/teachers")
            .body()

        return response.map { dto ->
            dto.toTeacher()
        }
    }


    override suspend fun enrollCurrentUser(
        courseId: Int,
        teacherId: Int
    ) {
        val token = TokenManager.getFirebaseToken()
        if (token.isBlank()) {
            throw Exception("Пользователь не авторизован")
        }


        val user = auth.currentUser
        val email = user?.email ?: throw Exception("Email не найден")
        val firebaseUid = user.uid
        val displayName = user?.displayName ?: ""
        val nameParts = displayName.split(" ")
        val firstName = nameParts.getOrNull(0) ?: ""
        val lastName = nameParts.getOrNull(1) ?: ""

        val response = client.post("$baseUrl/enrollments/secure") {
            header(HttpHeaders.Authorization, "Bearer $token")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(
                EnrollRequestDto(
                    firebaseUid = firebaseUid,
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    courseId = courseId,
                    teacherId = teacherId
                )
            )
        }

        if (!response.status.isSuccess()) {
            throw Exception("Ошибка записи на курс: ${response.status}")
        }
    }

    override suspend fun cancelCurrentUserEnrollment(courseId: Int) {
        val token = TokenManager.getFirebaseToken()
        if (token.isBlank()) {
            throw Exception("Пользователь не авторизован")
        }

        val response = client.delete("$baseUrl/enrollments/me/$courseId") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        if (!response.status.isSuccess()) {
            throw Exception("Ошибка отмены записи: ${response.status}")
        }
    }

    override suspend fun getCurrentUserEnrollments(): List<Enrollment> {
        val token = TokenManager.getFirebaseToken()
        if (token.isBlank()) {
            return emptyList()
        }

        val response = client.get("$baseUrl/enrollments/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        if (!response.status.isSuccess()) {
            return emptyList()
        }

        val responseList: List<EnrollmentDto> = response.body()

        return responseList.map { dto ->
            Enrollment(
                id = dto.id,
                userId = dto.userId,
                course = dto.course.toCourse(),
                teacher = dto.teacher?.toTeacher(),
                enrolledAt = dto.enrolledAt
            )
        }
    }

    override suspend fun getCurrentUserCourses(): List<Course> {
        return getCurrentUserEnrollments().map { enrollment ->
            enrollment.course
        }
    }

    private fun CourseDto.toCourse(): Course {
        return Course(
            id = id,
            title = title,
            description = description,
            duration = duration,
            category = category
        )
    }

    private fun TeacherDto.toTeacher(): Teacher {
        return Teacher(
            id = id,
            fullName = fullName,
            position = position,
            courseId = courseId
        )
    }
}