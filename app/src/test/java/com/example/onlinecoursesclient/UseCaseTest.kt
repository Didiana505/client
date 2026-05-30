package com.example.onlinecoursesclient

import com.example.onlinecoursesclient.domain.model.AuthResult
import com.example.onlinecoursesclient.domain.model.Course
import com.example.onlinecoursesclient.domain.model.Teacher
import com.example.onlinecoursesclient.domain.model.User
import com.example.onlinecoursesclient.domain.repository.AuthRepository
import com.example.onlinecoursesclient.domain.repository.CourseRepository
import com.example.onlinecoursesclient.domain.repository.UserRepository
import com.example.onlinecoursesclient.domain.usecase.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

// МОКИ ДЛЯ ТЕСТОВ

class MockAuthRepository : AuthRepository {
    private var registeredUser: AuthResult? = null
    private var loggedInUser: AuthResult? = null

    override suspend fun login(email: String, password: String): Result<AuthResult> {
        // Только test@mail.ru / 123456 проходят
        return if (email == "test@mail.ru" && password == "123456") {
            loggedInUser = AuthResult("uid123", email, "Test User")
            Result.success(loggedInUser!!)
        } else {
            Result.failure(Exception("Неправильный логин или пароль"))
        }
    }

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Result<AuthResult> {
        return when {
            email == "existing@mail.ru" -> Result.failure(Exception("Пользователь с таким email уже существует"))
            password.length < 6 -> Result.failure(Exception("Пароль должен содержать минимум 6 символов"))
            else -> {
                registeredUser = AuthResult("uid456", email, "$firstName $lastName")
                Result.success(registeredUser!!)
            }
        }
    }

    override fun logout() { loggedInUser = null }
    override fun getCurrentUserId(): String? = loggedInUser?.userId
}

class MockCourseRepository : CourseRepository {
    private val courses = listOf(
        Course(1, "Kotlin для начинающих", "Изучение Kotlin", "20 часов", "Программирование"),
        Course(2, "Android разработка", "Создание приложений", "30 часов", "Мобильная разработка"),
        Course(3, "Jetpack Compose", "Современный UI", "15 часов", "Программирование")
    )

    private val teachers = mapOf(
        1 to listOf(Teacher(1, "Иванов Иван", "Старший преподаватель", 1), Teacher(2, "Петрова Анна", "Преподаватель", 1)),
        2 to listOf(Teacher(3, "Сидоров Алексей", "Доцент", 2))
    )

    private var enrollments = mutableListOf<Pair<Int, Int>>()
    private var enrollmentWithTeacher = mutableListOf<Triple<Int, Int, Int>>()

    override suspend fun getCourses(): List<Course> = courses
    override suspend fun getTeachersByCourseId(courseId: Int): List<Teacher> = teachers[courseId] ?: emptyList()

    override suspend fun enrollCurrentUser(courseId: Int, teacherId: Int) {
        val userId = 1
        if (enrollments.any { it.first == userId && it.second == courseId }) {
            throw Exception("Пользователь уже записан на этот курс")
        }
        val teacher = teachers.values.flatten().find { it.id == teacherId }
            ?: throw Exception("Преподаватель не найден")
        if (teacher.courseId != courseId) {
            throw Exception("Преподаватель не ведёт этот курс")
        }
        enrollments.add(userId to courseId)
        enrollmentWithTeacher.add(Triple(userId, courseId, teacherId))
    }

    override suspend fun cancelCurrentUserEnrollment(courseId: Int) {
        val userId = 1
        if (!enrollments.removeIf { it.first == userId && it.second == courseId }) {
            throw Exception("Запись на курс не найдена")
        }
        enrollmentWithTeacher.removeIf { it.first == userId && it.second == courseId }
    }

    override suspend fun getCurrentUserEnrollments(): List<com.example.onlinecoursesclient.domain.model.Enrollment> {
        val userId = 1
        return enrollments.filter { it.first == userId }.map { enrollment ->
            val course = courses.find { it.id == enrollment.second }!!
            val teacherTriple = enrollmentWithTeacher.find { it.first == userId && it.second == enrollment.second }
            val teacher = teacherTriple?.let { teachers.values.flatten().find { t -> t.id == it.third } }
            com.example.onlinecoursesclient.domain.model.Enrollment(
                id = enrollment.second,
                userId = userId,
                course = course,
                teacher = teacher,
                enrolledAt = "2024-01-01T12:00:00"
            )
        }
    }

    override suspend fun getCurrentUserCourses(): List<Course> = getCurrentUserEnrollments().map { it.course }
}

class MockUserRepository : UserRepository {
    private var currentUser: User? = null

    override suspend fun getCurrentUser(): User? = currentUser

    override suspend fun updateCurrentUserAge(age: Int): User? {
        if (age < 1 || age > 120) throw Exception("Некорректный возраст")
        currentUser = currentUser?.copy(age = age) ?: User("uid123", "test@mail.ru", "Иван", "Петров", age)
        return currentUser
    }

    override suspend fun getCurrentUserProfile(): com.example.onlinecoursesclient.domain.model.UserProfile {
        return com.example.onlinecoursesclient.domain.model.UserProfile(
            displayName = "Иван Петров",
            email = "test@mail.ru",
            firstName = "Иван",
            lastName = "Петров",
            avatarLetter = "И"
        )
    }

    override suspend fun logout() { currentUser = null }
    fun setCurrentUser(user: User) { currentUser = user }
}

// ТЕСТЫ

class UseCaseBusinessTest {

    // ЛОГИН
    @Test
    fun `LoginUseCase - успешный вход с корректными данными`() = runTest {
        val mockRepo = MockAuthRepository()
        val loginUseCase = LoginUseCase(mockRepo)
        val result = loginUseCase("test@mail.ru", "123456")

        assertTrue(result.isSuccess)
        assertEquals("test@mail.ru", result.getOrNull()?.email)
    }

    @Test
    fun `LoginUseCase - ошибка при пустом email`() = runTest {
        val mockRepo = MockAuthRepository()
        val loginUseCase = LoginUseCase(mockRepo)
        val result = loginUseCase("", "123456")

        assertTrue(result.isFailure)
        assertEquals("Заполните email и пароль", result.exceptionOrNull()?.message)
    }

    @Test
    fun `LoginUseCase - ошибка при пустом пароле`() = runTest {
        val mockRepo = MockAuthRepository()
        val loginUseCase = LoginUseCase(mockRepo)
        val result = loginUseCase("test@mail.ru", "")

        assertTrue(result.isFailure)
        assertEquals("Заполните email и пароль", result.exceptionOrNull()?.message)
    }

    @Test
    fun `LoginUseCase - ошибка при неверном пароле`() = runTest {
        val mockRepo = MockAuthRepository()
        val loginUseCase = LoginUseCase(mockRepo)
        val result = loginUseCase("test@mail.ru", "wrongpassword")

        assertTrue(result.isFailure)
        assertEquals("Неправильный логин или пароль", result.exceptionOrNull()?.message)
    }

    // РЕГИСТРАЦИЯ
    @Test
    fun `RegisterUseCase - успешная регистрация с корректными данными`() = runTest {
        val mockRepo = MockAuthRepository()
        val registerUseCase = RegisterUseCase(mockRepo)
        val result = registerUseCase("Иван", "Петров", "newuser@mail.ru", "123456")

        assertTrue(result.isSuccess)
        assertEquals("Иван Петров", result.getOrNull()?.displayName)
    }

    @Test
    fun `RegisterUseCase - ошибка при пустом имени`() = runTest {
        val mockRepo = MockAuthRepository()
        val registerUseCase = RegisterUseCase(mockRepo)
        val result = registerUseCase("", "Петров", "test@mail.ru", "123456")

        assertTrue(result.isFailure)
        assertEquals("Заполните все поля", result.exceptionOrNull()?.message)
    }

    @Test
    fun `RegisterUseCase - ошибка при коротком пароле`() = runTest {
        val mockRepo = MockAuthRepository()
        val registerUseCase = RegisterUseCase(mockRepo)
        val result = registerUseCase("Иван", "Петров", "test@mail.ru", "123")

        assertTrue(result.isFailure)
        assertEquals("Пароль должен быть не короче 6 символов", result.exceptionOrNull()?.message)
    }

    @Test
    fun `RegisterUseCase - ошибка при существующем email`() = runTest {
        val mockRepo = MockAuthRepository()
        val registerUseCase = RegisterUseCase(mockRepo)
        val result = registerUseCase("Иван", "Петров", "existing@mail.ru", "123456")

        assertTrue(result.isFailure)
        assertEquals("Пользователь с таким email уже существует", result.exceptionOrNull()?.message)
    }

    // КУРСЫ
    @Test
    fun `GetCoursesUseCase - возвращает список курсов`() = runTest {
        val mockRepo = MockCourseRepository()
        val getCoursesUseCase = GetCoursesUseCase(mockRepo)
        val courses = getCoursesUseCase()

        assertEquals(3, courses.size)
        assertEquals("Kotlin для начинающих", courses[0].title)
    }

    // ПРЕПОДАВАТЕЛИ
    @Test
    fun `GetTeachersByCourseUseCase - возвращает преподавателей курса`() = runTest {
        val mockRepo = MockCourseRepository()
        val getTeachersUseCase = GetTeachersByCourseUseCase(mockRepo)
        val teachers = getTeachersUseCase(1)

        assertEquals(2, teachers.size)
        assertEquals("Иванов Иван", teachers[0].fullName)
    }

    // ЗАПИСЬ НА КУРС
    @Test
    fun `EnrollCurrentUserUseCase - успешная запись на курс`() = runTest {
        val mockRepo = MockCourseRepository()
        val enrollUseCase = EnrollCurrentUserUseCase(mockRepo)

        enrollUseCase(1, 1)

        val enrollments = mockRepo.getCurrentUserEnrollments()
        assertEquals(1, enrollments.size)
        assertEquals(1, enrollments[0].course.id)
    }

    @Test
    fun `EnrollCurrentUserUseCase - ошибка при повторной записи`() = runTest {
        val mockRepo = MockCourseRepository()
        val enrollUseCase = EnrollCurrentUserUseCase(mockRepo)

        enrollUseCase(1, 1)
        val result = runCatching { enrollUseCase(1, 1) }

        assertTrue(result.isFailure)
        assertEquals("Пользователь уже записан на этот курс", result.exceptionOrNull()?.message)
    }

    // ОТМЕНА ЗАПИСИ
    @Test
    fun `CancelCurrentUserEnrollmentUseCase - успешная отмена записи`() = runTest {
        val mockRepo = MockCourseRepository()
        val enrollUseCase = EnrollCurrentUserUseCase(mockRepo)
        val cancelUseCase = CancelCurrentUserEnrollmentUseCase(mockRepo)

        enrollUseCase(1, 1)
        assertEquals(1, mockRepo.getCurrentUserEnrollments().size)

        cancelUseCase(1)
        assertTrue(mockRepo.getCurrentUserEnrollments().isEmpty())
    }

    // ПРОФИЛЬ
    @Test
    fun `GetCurrentUserUseCase - возвращает пользователя если он существует`() = runTest {
        val mockRepo = MockUserRepository()
        val getUserUseCase = GetCurrentUserUseCase(mockRepo)
        mockRepo.setCurrentUser(User("uid123", "test@mail.ru", "Иван", "Петров", 25))

        val user = getUserUseCase()

        assertNotNull(user)
        assertEquals("Иван", user?.firstName)
        assertEquals(25, user?.age)
    }

    @Test
    fun `GetCurrentUserUseCase - возвращает null если пользователь не авторизован`() = runTest {
        val mockRepo = MockUserRepository()
        val getUserUseCase = GetCurrentUserUseCase(mockRepo)

        val user = getUserUseCase()

        assertNull(user)
    }

    // ВОЗРАСТ
    @Test
    fun `UpdateCurrentUserAgeUseCase - успешное обновление возраста`() = runTest {
        val mockRepo = MockUserRepository()
        val updateAgeUseCase = UpdateCurrentUserAgeUseCase(mockRepo)

        mockRepo.setCurrentUser(User("uid123", "test@mail.ru", "Иван", "Петров", null))
        val updatedUser = updateAgeUseCase(30)

        assertEquals(30, updatedUser?.age)
    }

    @Test
    fun `UpdateCurrentUserAgeUseCase - ошибка при некорректном возрасте`() = runTest {
        val mockRepo = MockUserRepository()
        val updateAgeUseCase = UpdateCurrentUserAgeUseCase(mockRepo)

        mockRepo.setCurrentUser(User("uid123", "test@mail.ru", "Иван", "Петров", null))
        val result = runCatching { updateAgeUseCase(0) }

        assertTrue(result.isFailure)
        assertEquals("Некорректный возраст", result.exceptionOrNull()?.message)
    }

    // ФОРМАТИРОВАНИЕ ТЕКСТА
    @Test
    fun `GetParagraphsUseCase - разбивает текст на абзацы`() {
        val getParagraphsUseCase = GetParagraphsUseCase()
        val text = "Первый абзац.\n\nВторой абзац.\n\nТретий абзац."
        val paragraphs = getParagraphsUseCase(text)

        assertEquals(3, paragraphs.size)
    }

    @Test
    fun `GetParagraphsUseCase - возвращает один абзац если нет пустых строк`() {
        val getParagraphsUseCase = GetParagraphsUseCase()
        val text = "Один сплошной абзац без пустых строк."
        val paragraphs = getParagraphsUseCase(text)

        assertEquals(1, paragraphs.size)
        assertEquals(text, paragraphs[0])
    }

    @Test
    fun `GetShortDescriptionUseCase - возвращает первые 2 предложения`() {
        val getShortDescriptionUseCase = GetShortDescriptionUseCase()
        val text = "Первое предложение. Второе предложение. Третье предложение."
        val shortDescription = getShortDescriptionUseCase(text, maxSentences = 2)

        assertEquals("Первое предложение. Второе предложение.", shortDescription)
    }

    @Test
    fun `GetShortDescriptionUseCase - возвращает полный текст если он короче 2 предложений`() {
        val getShortDescriptionUseCase = GetShortDescriptionUseCase()
        val text = "Одно короткое предложение."
        val shortDescription = getShortDescriptionUseCase(text, maxSentences = 2)

        assertEquals(text, shortDescription)
    }
}