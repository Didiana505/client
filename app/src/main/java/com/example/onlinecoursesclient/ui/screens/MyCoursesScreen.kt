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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.onlinecoursesclient.ui.viewmodel.CoursesViewModel
import androidx.compose.material3.Button
import com.example.onlinecoursesclient.domain.usecase.GetShortDescriptionUseCase

@Composable
fun MyCoursesScreen(
    modifier: Modifier = Modifier,
    viewModel: CoursesViewModel,
    onCourseClick: (Int) -> Unit
) {
    val myCourses by viewModel.myCourses.collectAsState()


    val getShortDescription = remember { GetShortDescriptionUseCase() }

    LaunchedEffect(Unit) {
        viewModel.loadCurrentUserCourses()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        item {
            Text(
                text = "Мои курсы",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        if (myCourses.isEmpty()) {
            item {
                Text(
                    text = "Вы пока не записаны ни на один курс",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            items(myCourses) { course ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            onCourseClick(course.id)
                        }
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
                            maxLines = 3,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Длительность: ${course.duration}")

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.cancelCurrentUserEnrollment(course.id)
                            }
                        ) {
                            Text("Отменить запись")
                        }
                    }
                }
            }
        }
    }
}