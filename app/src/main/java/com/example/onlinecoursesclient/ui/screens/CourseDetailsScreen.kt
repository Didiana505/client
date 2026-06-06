package com.example.onlinecoursesclient.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.onlinecoursesclient.domain.usecase.GetParagraphsUseCase
import com.example.onlinecoursesclient.ui.viewmodel.CoursesViewModel

@Composable
fun CourseDetailsScreen(
    modifier: Modifier = Modifier,
    courseId: Int,
    viewModel: CoursesViewModel,
    onBackClick: () -> Unit
) {
    val courses by viewModel.courses.collectAsState()
    val myCourses by viewModel.myCourses.collectAsState()
    val myEnrollments by viewModel.myEnrollments.collectAsState()
    val showCancelDialog by viewModel.showCancelDialog.collectAsState()
    val pendingCancelCourseId by viewModel.pendingCancelCourseId.collectAsState()

    val getParagraphs = remember { GetParagraphsUseCase() }

    LaunchedEffect(Unit) {
        if (courses.isEmpty()) {
            viewModel.loadCourses()
        }
        viewModel.loadCurrentUserCourses()
        viewModel.loadCurrentUserEnrollments()
    }

    val course = courses.find { it.id == courseId }
        ?: myCourses.find { it.id == courseId }

    val enrollment = myEnrollments.find { enrollment ->
        enrollment.course.id == courseId
    }

    val teacher = enrollment?.teacher

    // Диалог подтверждения отмены
    if (showCancelDialog && pendingCancelCourseId == courseId) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissCancelDialog() },
            title = { Text("Подтверждение") },
            text = { Text("Вы уверены, что хотите отменить запись?") },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmCancelEnrollment() }) {
                    Text("Да")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissCancelDialog() }) {
                    Text("Нет")
                }
            }
        )
    }

    if (course == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад"
                    )
                }

                Text(
                    text = "Курс не найден",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    } else {
        val scrollState = rememberScrollState()

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад"
                    )
                }

                Text(
                    text = course.title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Описание курса",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            val paragraphs = getParagraphs(course.description)
            Column {
                paragraphs.forEachIndexed { index, paragraph ->
                    if (paragraph.isNotBlank()) {
                        Text(
                            text = paragraph.trim(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (index < paragraphs.size - 1) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Длительность: ${course.duration}",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (enrollment != null) {
                Text(
                    text = "Вы уже записаны на этот курс",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Выбранный преподаватель",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (teacher != null) {
                    Text(
                        text = teacher.fullName,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = teacher.position,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            viewModel.onCancelEnrollmentClicked(course.id)
                        }
                    ) {
                        Text("Отменить запись")
                    }
                } else {
                    Text(
                        text = "Преподаватель не выбран",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Text(
                    text = "Вы пока не записаны на этот курс",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}