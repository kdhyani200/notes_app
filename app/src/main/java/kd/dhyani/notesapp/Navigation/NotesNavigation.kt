package kd.dhyani.notesapp.Navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kd.dhyani.notesapp.Screens.InsertNotesScreen
import kd.dhyani.notesapp.Screens.MainScreen

@Composable
fun NotesNavigation(navHostController: NavHostController){
    NavHost(navController = navHostController, startDestination = "home",
        enterTransition = {
            fadeIn(animationSpec = tween(500)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                tween((500))
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(500)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                tween((500))
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(500)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                tween((500))
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(500)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                tween((500))
            )
        }
        ){
        composable(NotesNavigationItem.HomeScreen.route) {
            MainScreen(navHostController)
        }

        composable(NotesNavigationItem.InsertNotesScreen.route+"/{id}") {
            val id = it.arguments?.getString("id")
            InsertNotesScreen(navHostController,id)
        }
    }
}
