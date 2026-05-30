package com.example.onlinecoursesclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.onlinecoursesclient.navigation.NavGraph
import com.example.onlinecoursesclient.ui.theme.OnlineCoursesClientTheme
import com.example.onlinecoursesclient.ui.viewmodel.CoursesViewModel
import com.example.onlinecoursesclient.ui.viewmodel.ProfileViewModel
import com.example.onlinecoursesclient.utils.AppContainer

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            val coursesViewModel: CoursesViewModel = viewModel(
                factory = AppContainer.getCoursesViewModelFactory()
            )

            val profileViewModel: ProfileViewModel = viewModel(
                factory = AppContainer.getProfileViewModelFactory(application)
            )

            val isDarkTheme = profileViewModel.isDarkTheme.collectAsState()

            OnlineCoursesClientTheme(
                darkTheme = isDarkTheme.value
            ) {
                NavGraph(
                    navController = navController,
                    coursesViewModel = coursesViewModel,
                    profileViewModel = profileViewModel,
                    authViewModelFactory = AppContainer.getAuthViewModelFactory()
                )
            }
        }
    }
}