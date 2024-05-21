package com.example.inkspire_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen {
                //navigates to mainactivity after the animation ends
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish() //this is so the user cannot return back to this screen
            }
        }
    }
}

@Composable
fun SplashScreen(onAnimationEnd: () -> Unit) {
// defines the letters to be displayed one letter at a time
    val letters = "Inkspire".toCharArray()
    var visibleLetters by remember { mutableStateOf(listOf<Char>()) }

    //this handles the animation of the letters and adds the delay
    LaunchedEffect(Unit) {
        letters.forEach { letter ->
            visibleLetters = visibleLetters + letter
            delay(200)
        }

        //additional delay before the splash screen ends
        delay(1000)
        onAnimationEnd()
    }

    //background
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White // White background for splash screen
    ) {
        //centres the text
        Box(contentAlignment = Alignment.Center) {
            Row {
                letters.forEachIndexed { index, letter ->
                    AnimatedVisibility(
                        visible = visibleLetters.contains(letter),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = letter.toString(),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6200EE) //purple color for text
                        )
                    }
                }
            }
        }
    }
}
