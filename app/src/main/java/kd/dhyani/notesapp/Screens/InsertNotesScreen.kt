package kd.dhyani.notesapp.Screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import kd.dhyani.notesapp.Models.Notes
import kd.dhyani.notesapp.ui.theme.NotesAppTheme
import kd.dhyani.notesapp.ui.theme.black
import kd.dhyani.notesapp.ui.theme.colorGrey
import kd.dhyani.notesapp.ui.theme.contentcard

@Composable
fun InsertNotesScreen(navHostController: NavHostController, id: String?) {
    NotesAppTheme { // Wrap the entire screen with the theme
        val context = LocalContext.current
        val db = FirebaseFirestore.getInstance()
        val notesDBref = db.collection("notes")
        val focusManager = LocalFocusManager.current

        val title = remember { mutableStateOf("") }
        val description = remember { mutableStateOf("") }

        LaunchedEffect(key1 = id) {
            if (id != null && id != "defaultId") {
                notesDBref.document(id).get().addOnSuccessListener {
                    val singleData = it.toObject(Notes::class.java)
                    if (singleData != null) {
                        title.value = singleData.title
                        description.value = singleData.description
                    }
                }
            } else {
                title.value = ""
                description.value = ""
            }
        }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier.padding(5.dp, 20.dp),
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(corner = CornerSize(100.dp)),
                    onClick = {
                        if (title.value.isEmpty() && description.value.isEmpty()) {
                            Toast.makeText(context, "Fill both fields", Toast.LENGTH_SHORT).show()
                        } else {
                            val myNotesId = if (id != "defaultId") {
                                id.toString()
                            } else {
                                notesDBref.document().id
                            }

                            val createdAt = System.currentTimeMillis() // Generate timestamp

                            val notes = Notes(
                                id = myNotesId,
                                title = title.value,
                                description = description.value,
                                createdAt = createdAt // Include timestamp
                            )

                            notesDBref.document(myNotesId).set(notes).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(context, "Note created", Toast.LENGTH_SHORT).show()
                                    navHostController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Done, contentDescription = "")
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus() // Dismiss keyboard when tapping outside
                        })
                    }
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Text(
                        modifier = Modifier.padding(10.dp, 5.dp),
                        text = "Insert Note",
                        style = TextStyle(
                            fontSize = 32.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Divider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    TextField(
                        textStyle = TextStyle(MaterialTheme.colorScheme.onSurface, fontSize = 20.sp), // Use theme color
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = contentcard,
                            unfocusedContainerColor = contentcard,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = black,
                            unfocusedTextColor = black
                        ),
                        value = title.value,
                        onValueChange = { title.value = it },
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(corner = CornerSize(15.dp)),
                        placeholder = { Text("Enter Your title", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) }
                    )

                    TextField(
                        textStyle = TextStyle(MaterialTheme.colorScheme.onSurface, fontSize = 15.sp), // Use theme color
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = contentcard,
                            unfocusedContainerColor = contentcard,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = black,
                            unfocusedTextColor = black
                        ),
                        value = description.value,
                        onValueChange = { description.value = it },
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .fillMaxHeight(0.6f),
                        shape = RoundedCornerShape(corner = CornerSize(15.dp)),
                        placeholder = { Text("Enter Your description", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) }
                    )
                }
            }
        }
    }
}