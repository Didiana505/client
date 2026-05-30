package com.example.onlinecoursesclient.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlinecoursesclient.domain.model.Course
import com.example.onlinecoursesclient.domain.model.Enrollment
import com.example.onlinecoursesclient.domain.model.Teacher
import com.example.onlinecoursesclient.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CoursesViewModel(
    private val getCoursesUseCase: GetCoursesUseCase,
    private val getTeachersByCourseUseCase: GetTeachersByCourseUseCase,
    private val enrollCurrentUserUseCase: EnrollCurrentUserUseCase,
    private val cancelCurrentUserEnrollmentUseCase: CancelCurrentUserEnrollmentUseCase,
    private val getCurrentUserEnrollmentsUseCase: GetCurrentUserEnrollmentsUseCase,
) : ViewModel() {

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    private val _teachers = MutableStateFlow<List<Teacher>>(emptyList())
    val teachers: StateFlow<List<Teacher>> = _teachers.asStateFlow()

    private val _myEnrollments = MutableStateFlow<List<Enrollment>>(emptyList())
    val myEnrollments: StateFlow<List<Enrollment>> = _myEnrollments.asStateFlow()

    private val _myCourses = MutableStateFlow<List<Course>>(emptyList())
    val myCourses: StateFlow<List<Course>> = _myCourses.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _enrollSuccess = MutableStateFlow(false)
    val enrollSuccess: StateFlow<Boolean> = _enrollSuccess.asStateFlow()

    fun loadCourses() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _courses.value = getCoursesUseCase()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadTeachersForCourse(courseId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _teachers.value = getTeachersByCourseUseCase(courseId)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun enrollCurrentUser(
        courseId: Int,
        teacherId: Int,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _enrollSuccess.value = false
            try {
                enrollCurrentUserUseCase(
                    courseId = courseId,
                    teacherId = teacherId
                )
                _enrollSuccess.value = true
                loadCurrentUserEnrollments()
                loadCurrentUserCourses()
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cancelCurrentUserEnrollment(courseId: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                cancelCurrentUserEnrollmentUseCase(courseId)
                loadCurrentUserEnrollments()
                loadCurrentUserCourses()
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCurrentUserEnrollments() {
        viewModelScope.launch {
            try {
                _myEnrollments.value = getCurrentUserEnrollmentsUseCase()
            } catch (e: Exception) {
                _myEnrollments.value = emptyList()
            }
        }
    }

    fun loadCurrentUserCourses() {
        viewModelScope.launch {
            try {
                val enrollments = getCurrentUserEnrollmentsUseCase()
                _myCourses.value = enrollments.map { it.course }
            } catch (e: Exception) {
                _myCourses.value = emptyList()
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun resetEnrollSuccess() {
        _enrollSuccess.value = false
    }
}