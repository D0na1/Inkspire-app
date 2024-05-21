package com.example.inkspire_app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

//an enum class is defined for all the screens
enum class AppScreens(val title: String) {
    ViewEntries("View Entries"),
    CreateEntry("Create Entry"),
    Settings("Settings")
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    //gets the current back stack entry and route to determine the selected screen
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        //iterates over each screen defined in the AppScreens enum
        AppScreens.values().forEach { screen ->
            //creates the navigation bar item for each screen
            NavigationBarItem(
                //defines the icon for each screen
                icon = {
                    when (screen) {
                        AppScreens.ViewEntries -> Icon(Icons.Default.Menu, contentDescription = screen.title)
                        AppScreens.CreateEntry -> Icon(Icons.Default.Create, contentDescription = screen.title)
                        AppScreens.Settings -> Icon(Icons.Default.Settings, contentDescription = screen.title)
                    }
                },
                //creates the label for each screen
                label = { Text(screen.title) },
                //determines if the current item is the one selected and this is based on the route
                selected = currentRoute == screen.name,
                onClick = {
                    navController.navigate(screen.name) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                //setting the colours for the selected and unselected icons
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Blue,
                    unselectedIconColor = Color.Gray
                )
            )
        }
    }
}
