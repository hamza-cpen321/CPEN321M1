package com.example.cpen321application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import com.example.cpen321application.ui.theme.CPEN321ApplicationTheme

class MainActivity4 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CPEN321ApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TimerScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun TimerScreen(modifier: Modifier = Modifier) {
    var minutes by remember { mutableStateOf("") }
    var seconds by remember { mutableStateOf("") }
    var remainingSeconds by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning && remainingSeconds > 0) {
            delay(1000)
            remainingSeconds--
        }
        if (isRunning && remainingSeconds == 0) {
            isRunning = false
            showConfetti = true
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = formatTime(remainingSeconds))
        OutlinedTextField(
            value = minutes,
            onValueChange = { minutes = it.filter(Char::isDigit) },
            label = { Text("Minutes") }
        )
        OutlinedTextField(
            value = seconds,
            onValueChange = { seconds = it.filter(Char::isDigit) },
            label = { Text("Seconds") }
        )
        Button(onClick = {
            remainingSeconds = minutes.toIntOrNull().orZero() * 60 + seconds.toIntOrNull().orZero()
            isRunning = remainingSeconds > 0
            showConfetti = false
        }) {
            Text(if (isRunning) "Restart" else "Start")
        }
        Button(onClick = {
            isRunning = false
            remainingSeconds = 0
            showConfetti = false
        }) {
            Text("Reset")
        }
        if (showConfetti) {
            Image(
                painter = painterResource(R.drawable.confetti),
                contentDescription = "Confetti"
            )
        }
    }
}

private fun Int?.orZero(): Int = this ?: 0

private fun formatTime(totalSeconds: Int): String =
    "%02d:%02d".format(totalSeconds / 60, totalSeconds % 60)

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    CPEN321ApplicationTheme {
        TimerScreen()
    }
}

