package com.example.onlinecoursesclient.ui.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.onlinecoursesclient.domain.model.Course
import com.example.onlinecoursesclient.domain.model.Teacher

@Composable
fun TeacherSelectionDialog(
    course: Course,
    teachers: List<Teacher>,
    onDismiss: () -> Unit,
    onTeacherSelected: (Teacher) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Выберите преподавателя") },
        text = {
            Column {
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (teachers.isEmpty()) {
                    Text(
                        text = "Преподаватели загружаются или не найдены",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    teachers.forEach { teacher ->
                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onTeacherSelected(teacher) }
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = teacher.fullName,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = teacher.position,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}