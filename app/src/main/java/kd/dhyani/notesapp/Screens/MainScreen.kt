package kd.dhyani.notesapp.Screens

import android.app.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kd.dhyani.notesapp.Models.Notes
import kd.dhyani.notesapp.Navigation.NotesNavigationItem
import kd.dhyani.notesapp.R
import kd.dhyani.notesapp.ui.theme.*

@Composable
fun MainScreen(navHostController: NavHostController) {
    NotesAppTheme {
        val db = FirebaseFirestore.getInstance()
        val notesDBref = db.collection("notes")

        val noteList = remember { mutableStateListOf<Notes>() }
        val datavalue = remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            notesDBref.orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if (error == null) {
                        val data = value?.toObjects(Notes::class.java)
                        noteList.clear()
                        if (data != null) {
                            noteList.addAll(data)
                        }
                        datavalue.value = true
                    } else {
                        datavalue.value = false
                    }
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
                        navHostController.navigate(NotesNavigationItem.InsertNotesScreen.route + "/defaultId")
                    }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "")
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Text(
                        modifier = Modifier.padding(10.dp, 5.dp),
                        text = "Notes",
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

                    if (datavalue.value) {
                        if (noteList.isEmpty()) {
                            NoDataAnimation()
                        } else {
                            LazyColumn {
                                items(noteList) { notes ->
                                    ListItems(notes, notesDBref, navHostController)
                                }
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(25.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun NoDataAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_data))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(250.dp)
        )
    }
}

@Composable
fun ListItems(notes: Notes, notesDBref: CollectionReference, navHostController: NavHostController) {

    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(20.dp)))
            .background(color = contentcard)
    ) {

        DropdownMenu(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.onTertiary)
                .width(80.dp)
                .clip(RoundedCornerShape(20.dp)),
            properties = PopupProperties(clippingEnabled = true),
            offset = DpOffset(x = 200.dp, y = (-70).dp),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(text = { Text(text = "Edit", style = TextStyle(MaterialTheme.colorScheme.onBackground), fontSize = 16.sp) }, onClick = {
                navHostController.navigate(NotesNavigationItem.InsertNotesScreen.route + "/${notes.id}")
                expanded = false
            })

            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            DropdownMenuItem(text = { Text(text = "Delete", style = TextStyle(MaterialTheme.colorScheme.onBackground), fontSize = 16.sp) },
                onClick = {
                    val alertDialog = AlertDialog.Builder(context)
                    alertDialog.setMessage("Are you sure you want to delete this note?")
                    alertDialog.setPositiveButton("Yes") { dialog, _ ->
                        notesDBref.document(notes.id).delete()
                        dialog?.dismiss()
                    }
                    alertDialog.setNegativeButton("No") { dialog, _ -> dialog?.dismiss() }
                    alertDialog.show()
                    expanded = false
                })
        }

        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "",
            tint = colorGrey,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(15.dp)
                .clickable {
                    expanded = true
                }
        )

        Column(modifier = Modifier.padding(15.dp)) {
            Text(
                text = notes.title, style = TextStyle(color = black, fontSize = 20.sp)
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Text(
                text = notes.description, style = TextStyle(color = colorGrey)
            )
        }
    }
}
