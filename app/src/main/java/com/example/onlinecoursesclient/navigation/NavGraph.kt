package com.example.onlinecoursesclient.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.onlinecoursesclient.ui.screens.CourseDetailsScreen
import com.example.onlinecoursesclient.ui.screens.CoursesScreen
import com.example.onlinecoursesclient.ui.screens.LoginScreen
import com.example.onlinecoursesclient.ui.screens.MyCoursesScreen
import com.example.onlinecoursesclient.ui.screens.ProfileScreen
import com.example.onlinecoursesclient.ui.viewmodel.AuthViewModel
import com.example.onlinecoursesclient.ui.viewmodel.CoursesViewModel
import com.example.onlinecoursesclient.ui.viewmodel.ProfileViewModel
import com.example.onlinecoursesclient.utils.AppContainer
import androidx.lifecycle.ViewModelProvider
sealed class Screen(val route: String, val title: String) {
    data object Login : Screen("login", "Вход")
    data object Courses : Screen("courses", "Курсы")
    data object MyCourses : Screen("my_courses", "Мои курсы")
    data object Profile : Screen("profile", "Профиль")

    data object CourseDetails : Screen("course_details/{courseId}", "О курсе") {
        fun createRoute(courseId: Int): String {
            return "course_details/$courseId"
        }
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    coursesViewModel: CoursesViewModel,
    profileViewModel: ProfileViewModel,
    authViewModelFactory: ViewModelProvider.Factory
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            val authViewModel: AuthViewModel = viewModel(
                factory = authViewModelFactory
            )
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Screen.Courses.route) {
            MainScaffold(navController = navController) { modifier ->
                CoursesScreen(
                    modifier = modifier,
                    viewModel = coursesViewModel,
                    onCourseClick = { courseId ->
                        navController.navigate(Screen.CourseDetails.createRoute(courseId))
                    }
                )
            }
        }

        composable(Screen.MyCourses.route) {
            MainScaffold(navController = navController) { modifier ->
                MyCoursesScreen(
                    modifier = modifier,
                    viewModel = coursesViewModel,
                    onCourseClick = { courseId ->
                        navController.navigate(Screen.CourseDetails.createRoute(courseId))
                    }
                )
            }
        }

        composable(Screen.Profile.route) {
            MainScaffold(navController = navController) { modifier ->
                ProfileScreen(
                    modifier = modifier,
                    navController = navController,
                    profileViewModel = profileViewModel
                )
            }
        }

        composable(
            route = Screen.CourseDetails.route,
            arguments = listOf(
                navArgument("courseId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt("courseId") ?: 0

            MainScaffold(navController = navController) { modifier ->
                CourseDetailsScreen(
                    modifier = modifier,
                    courseId = courseId,
                    viewModel = coursesViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
private fun MainScaffold(
    navController: NavHostController,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        content(Modifier.padding(paddingValues))
    }
}

@Composable
private fun BottomNavigationBar(
    navController: NavHostController
) {
    val items = listOf(
        Screen.Courses,
        Screen.MyCourses,
        Screen.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Courses.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    when (screen) {
                        Screen.Courses -> {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "Курсы"
                            )
                        }
                        Screen.MyCourses -> {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = "Мои курсы"
                            )
                        }
                        Screen.Profile -> {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Профиль"
                            )
                        }
                        else -> {}
                    }
                },
                label = { Text(screen.title) }
            )
        }
    }
}