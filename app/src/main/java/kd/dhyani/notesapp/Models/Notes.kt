package kd.dhyani.notesapp.Models

data class Notes(
    val id : String = "",
    val title : String = "",
    val description : String = "",
    val createdAt: Long = System.currentTimeMillis()
)
