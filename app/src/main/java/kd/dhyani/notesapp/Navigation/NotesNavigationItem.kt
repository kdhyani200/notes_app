package kd.dhyani.notesapp.Navigation

sealed class NotesNavigationItem (val route : String) {
    object HomeScreen : NotesNavigationItem(route = "home")
    object InsertNotesScreen : NotesNavigationItem(route = "create_notes")

}