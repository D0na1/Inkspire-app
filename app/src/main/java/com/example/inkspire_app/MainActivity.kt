package com.example.inkspire_app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.inkspire_app.ui.theme.InkspireTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //starts the notification service as a foreground service
        val serviceIntent = Intent(this, NotificationService::class.java)
        startForegroundService(serviceIntent)
        //requests notification permissions
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            0
        )
        setContent {
            val context = LocalContext.current
            val sharedPreferences =
                context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
            val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
            val darkTheme = remember { mutableStateOf(isDarkMode) }

            InkspireTheme(darkTheme = darkTheme.value) {
                //creates a navigation controller
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar(navController = navController) }
                ) { innerPadding ->
                    //sets up a Box layout to contain the navigation host
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavHost(
                            navController = navController,
                            startDestination = AppScreens.CreateEntry.name
                            //this is where the navigation routes and their corresponding composable functions are defined
                        ) {
                            composable(AppScreens.ViewEntries.name) { ViewEntries(navController = navController) }//passes the NavHostController to ViewEntries
                            composable(AppScreens.CreateEntry.name) {//passes the context of MainActivity to CreateEntry
                                CreateEntry(
                                    navController = navController,
                                    context = this@MainActivity
                                )
                            }
                            //defines the composable function for the EditEntry screen
                            composable("editEntry/{entryId}") { backStackEntry ->
                                val entryId =
                                    //retrieves the entryId from the navigation arguments
                                    backStackEntry.arguments?.getString("entryId")?.toInt()
                                        ?: return@composable
                                CreateEntry(
                                    navController = navController,
                                    context = this@MainActivity,
                                    entryId = entryId//passes the retrieved entryId to CreateEntry for editing
                                )
                            }
                            composable(AppScreens.Settings.name) {
                                Settings(navController = navController, darkTheme = darkTheme)
                            }
                        }
                    }
                }
            }//log the onCreate Lifecycle event
            Log.v("Activity Lifecycle Methods", "onCreate")
        }
    }
    //log the onStart Lifecycle event
    override fun onStart() {
        super.onStart()
        Log.v("Activity Lifecycle Methods", "onStart")
    }
    //log the onResume Lifecycle event
    override fun onResume() {
        super.onResume()
        Log.v("Activity Lifecycle Methods", "onResume")
    }
    //log the onPause Lifecycle event
    override fun onPause() {
        super.onPause()
        Log.v("Activity Lifecycle Methods", "onPause")
    }
    //log the onStop Lifecycle event
    override fun onStop() {
        super.onStop()
        Log.v("Activity Lifecycle Methods", "onStop")
    }
    //log the onDestroy Lifecycle event
    override fun onDestroy() {
        super.onDestroy()
        Log.v("Activity Lifecycle Methods", "onDestroy")
    }
}
