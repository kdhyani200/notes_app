package kd.dhyani.notesapp.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kd.dhyani.notesapp.Screens.InsertNotesScreen
import kd.dhyani.notesapp.Screens.MainScreen

@Composable
fun NotesNavigation(navHostController: NavHostController){
    NavHost(navController = navHostController, startDestination = "home"){
        composable(NotesNavigationItem.HomeScreen.route) {
            MainScreen(navHostController)
        }

        composable(NotesNavigationItem.InsertNotesScreen.route+"/{id}") {
            val id = it.arguments?.getString("id")
            InsertNotesScreen(navHostController,id)
        }
    }
}
