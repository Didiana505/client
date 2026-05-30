package com.example.onlinecoursesclient.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.onlinecoursesclient.domain.model.Course
import com.example.onlinecoursesclient.ui.viewmodel.CoursesViewModel
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.onlinecoursesclient.domain.usecase.GetShortDescriptionUseCase

@Composable
fun CoursesScreen(
    modifier: Modifier = Modifier,
    viewModel: CoursesViewModel,
    onCourseClick: (Int) -> Unit
) {
    val courses by viewModel.courses.collectAsState()
    val myCourses by viewModel.myCourses.collectAsState()
    val teachers by viewModel.teachers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()


    val getShortDescription = remember { GetShortDescriptionUseCase() }

    var showTeacherDialog by remember { mutableStateOf(false) }
    var selectedCourse by remember { mutableStateOf<Course?>(null) }

    var searchText by rememberSaveable { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Все") }
    var showCategoryDialog by remember { mutableStateOf(false) }

    val categories = remember(courses) {
        listOf("Все") + courses
            .map { it.category }
            .distinct()
            .sorted()
    }

    val filteredCourses = courses.filter { course ->
        val matchesSearch = searchText.isBlank() ||
                course.title.contains(searchText, ignoreCase = true) ||
                course.description.contains(searchText, ignoreCase = true)

        val matchesCategory = selectedCategory == "Все" ||
                course.category == selectedCategory

        matchesSearch && matchesCategory
    }

    LaunchedEffect(Unit) {
        viewModel.loadCourses()
        viewModel.loadCurrentUserCourses()
    }

    LaunchedEffect(error) {
        error?.let {
            viewModel.clearError()
        }
    }

    if (showTeacherDialog && selectedCourse != null) {
        TeacherSelectionDialog(
            course = selectedCourse!!,
            teachers = teachers,
            onDismiss = {
                showTeacherDialog = false
                selectedCourse = null
            },
            onTeacherSelected = { teacher ->
                viewModel.enrollCurrentUser(
                    courseId = selectedCourse!!.id,
                    teacherId = teacher.id
                )
                showTeacherDialog = false
                selectedCourse = null
            }
        )
    }

    if (showCategoryDialog) {
        CategoryFilterDialog(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { category ->
                selectedCategory = category
                showCategoryDialog = false
            },
            onDismiss = {
                showCategoryDialog = false
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        item {
            Text(
                text = "Доступные курсы",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            SearchBar(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                onFilterClick = { showCategoryDialog = true }
            )

            if (selectedCategory != "Все" && searchText.isBlank()) {
                Text(
                    text = "Категория: $selectedCategory",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (filteredCourses.isEmpty()) {
                Text(
                    text = "Курсы не найдены",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
            }
        }

        items(filteredCourses) { course ->
            val alreadyEnrolled = myCourses.any { myCourse ->
                myCourse.id == course.id
            }

            CourseCard(
                course = course,
                alreadyEnrolled = alreadyEnrolled,
                onCourseClick = { onCourseClick(course.id) },
                onEnrollClick = {
                    selectedCourse = course
                    viewModel.loadTeachersForCourse(course.id)
                    showTeacherDialog = true
                },
                getShortDescription = getShortDescription
            )
        }
    }
}

@Composable
private fun CourseCard(
    course: Course,
    alreadyEnrolled: Boolean,
    onCourseClick: () -> Unit,
    onEnrollClick: () -> Unit,
    getShortDescription: GetShortDescriptionUseCase
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onCourseClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = course.title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))


            Text(
                text = getShortDescription(course.description),
                maxLines = 4,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Длительность: ${course.duration}")

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Категория: ${course.category}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (alreadyEnrolled) {
                Text(
                    text = "Вы уже записаны",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Button(onClick = onEnrollClick) {
                    Text("Записаться")
                }
            }
        }
    }
}