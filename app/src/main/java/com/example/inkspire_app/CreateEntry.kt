package com.example.inkspire_app

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.inkspire_app.data.JournalEntry
import com.example.inkspire_app.data.JournalViewModel
import com.example.inkspire_app.ui.theme.InkspireTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEntry(
    context: Context,
    viewModel: JournalViewModel = viewModel(),
    navController: NavHostController,
    entryId: Int? = null
) {
    //variables for title and content fields
    var title by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    val entry by viewModel.getEntryById(entryId ?: 0).observeAsState()

    //prepopulates the fields if an entryID is provided- this is for editing
    LaunchedEffect(entryId, entry) {
        if (entryId != null && entry != null) {
            title = entry!!.title
            content = entry!!.content
        }
    }

    //function to show a toast message
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    InkspireTheme {
        Scaffold(
            topBar = {
                //top app bar with title and action buttons
                TopAppBar(
                    title = { Text("Create Entry", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF6200EA)
                    ),
                    actions = {
                        IconButton(onClick = {
                            //checks if any of the fields are empty
                            if (title.trim().isNotBlank() && content.trim().isNotBlank()) {
                                // creates a new journal entry object
                                val newEntry = JournalEntry(
                                    id = entryId ?: 0,
                                    title = title,
                                    content = content,
                                    timestamp = System.currentTimeMillis()
                                )
                                //update existing entry or create a new one
                                if (entryId != null) {
                                    viewModel.update(newEntry)
                                } else {
                                    viewModel.insert(newEntry)
                                }
                                //navigate back to the view entries screen and resets title and content fields
                                navController.navigate("viewEntries")
                                title = ""
                                content = ""
                            } else {
                                //if title or content is empty then shows a toast message
                                showToast(context, "Please enter a title and content!")
                            }
                        }) {
                            //after validating the inputs , either inserts a new entry or updates existing entries.
                            Icon(Icons.Default.Check, contentDescription = "Save", tint = Color.White)
                        }
                        //cancel button clears the fields
                        IconButton(onClick = {
                            navController.navigate("CreateEntry")
                            title = ""
                            content = ""
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.White)
                        }
                    }
                )
            },
            content = { padding ->
                //this column arranges the UI elements vertically
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Title",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                    BasicTextField(
                        value = title,
                        onValueChange = { title = it },
                        textStyle = TextStyle(fontSize = 20.sp, color = Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(Color(0xFFF2F2F2), shape = MaterialTheme.shapes.small)
                            .padding(16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Content",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                    BasicTextField(
                        value = content,
                        onValueChange = { content = it },
                        textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .background(Color(0xFFF2F2F2), shape = MaterialTheme.shapes.small)
                            .padding(14.dp)
                    )
                    Spacer(modifier = Modifier.height(25.dp))
                    Row(
                        //row for action buttons
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { shareJournalEntry(context, title, content, System.currentTimeMillis()) }
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color(0xFF6200EA)
                            )
                        }
                    }
                }
            }
        )
    }
}

private fun shareJournalEntry(context: Context, title: String, content: String, timestamp: Long) {
    //formats the timestamp for display
    val formattedTimestamp = formatTimestamp(timestamp)
    val shareText = """
        |**Title:** $title
        |
        |**Content:**
        |$content
        |
        |**Date:** $formattedTimestamp
        """.trimMargin()
//creates an intent to share the text
    val shareIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }
    //starts the share activity
    context.startActivity(Intent.createChooser(shareIntent, null))
}
