package com.example.inkspire_app

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.inkspire_app.data.JournalEntry
import com.example.inkspire_app.data.JournalViewModel
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
//observes journal entries from the view model
fun ViewEntries(viewModel: JournalViewModel = viewModel(), navController: NavHostController) {
    val entries by viewModel.allEntries.observeAsState(emptyList())
    var showCreateEntry by remember { mutableStateOf(false) }

    //main UI scaffold with top bar and floating action button
    Scaffold(
        topBar = {
            if (!showCreateEntry) {
                TopAppBar(
                    title = {
                        Text(
                            text = "View Entries",
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF6200EA)
                    )
                )
            }
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (!showCreateEntry) {
                    //displays the list of journal entries
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(entries, key = { it.id }) { entry ->
                            SwipeToDelete(
                                entry = entry,
                                onDelete = { viewModel.delete(entry) },
                                onEdit = { navController.navigate("editEntry/${entry.id}") },
                                modifier = Modifier
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .border(1.dp, Color.Gray, MaterialTheme.shapes.small)
                                    .padding(16.dp)
                            )
                        }
                    }
                }
                //show the CreateEntry screen with an animation
                AnimatedVisibility(
                    visible = showCreateEntry,
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it }
                ) {
                    CreateEntry(navController = navController, context = LocalContext.current)
                }
            }
        },
        floatingActionButton = {
            if (!showCreateEntry) {
                FloatingActionButton(
                    onClick = { showCreateEntry = true },
                    containerColor = Color(0xFF6200EA),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Create Entry")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDelete(
    //this composable function wraps each entry in a swipe-to-delete container,changes the background
    //to red and shows a delete icon when swiped
    entry: JournalEntry,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    //state to manage swipe-to-dismiss behavior
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToEnd || it == DismissValue.DismissedToStart) {
                onDelete()
            }
            true
        }
    )

    val lastTapTimestamp = remember { mutableStateOf(0L) }

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
        background = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.Default -> Color.Transparent
                    DismissValue.DismissedToEnd, DismissValue.DismissedToStart -> Color.Red
                }, label = ""
            )
            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1.0f,
                animationSpec = tween(300), label = ""
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.scale(scale),
                    tint = Color.White
                )
            }
        },
        dismissContent = {
            Column(
                modifier = modifier.clickable {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastTapTimestamp.value < 500) {
                        //detects double tap, which triggers the edit
                        onEdit()
                    }
                    lastTapTimestamp.value = currentTime
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = entry.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (entry.content.length > 50) "${entry.content.take(50)}..." else entry.content,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = formatTimestamp(entry.timestamp),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        IconButton(onClick = { onDelete() }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Entry",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    )
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
