package com.example.cpen321application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var showSurprise by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning && remainingSeconds > 0) {
            delay(1000)
            remainingSeconds--
        }
        if (isRunning && remainingSeconds == 0) {
            isRunning = false
            showSurprise = true
        }
    }

    if (showSurprise) {
        TicTacToeScreen(
            modifier = modifier,
            onBackToTimer = {
                showSurprise = false
                isRunning = false
                remainingSeconds = 0
                minutes = ""
                seconds = ""
            }
        )
        return
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
            showSurprise = false
        }) {
            Text(if (isRunning) "Restart" else "Start")
        }
        Button(onClick = {
            isRunning = false
            remainingSeconds = 0
            showSurprise = false
        }) {
            Text("Reset")
        }
    }
}

@Composable
fun TicTacToeScreen(modifier: Modifier = Modifier, onBackToTimer: () -> Unit = {}) {
    var board by remember { mutableStateOf(List(9) { "" }) }
    var currentPlayer by remember { mutableStateOf("X") }
    var winner by remember { mutableStateOf<String?>(null) }
    var draw by remember { mutableStateOf(false) }

    val statusText = when {
        winner != null -> "Player $winner wins!"
        draw -> "It's a draw!"
        else -> "Player $currentPlayer's turn"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Surprise!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Text(
            text = statusText,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (rowIndex in 0..2) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (colIndex in 0..2) {
                        val index = rowIndex * 3 + colIndex
                        Button(
                            onClick = {
                                if (board[index].isNotEmpty() || winner != null || draw) return@Button

                                val updatedBoard = board.toMutableList()
                                updatedBoard[index] = currentPlayer
                                board = updatedBoard

                                val nextWinner = findWinner(board)
                                if (nextWinner != null) {
                                    winner = nextWinner
                                    return@Button
                                }

                                if (board.all { it.isNotEmpty() }) {
                                    draw = true
                                    return@Button
                                }

                                currentPlayer = if (currentPlayer == "X") "O" else "X"
                            },
                            modifier = Modifier.size(80.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (board[index] == "X") Color(0xFF7C9EFF) else Color(0xFFFFB74D)
                            )
                        ) {
                            Text(
                                text = board[index],
                                fontSize = 28.sp
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                board = List(9) { "" }
                currentPlayer = "X"
                winner = null
                draw = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Play Again")
        }

        Button(
            onClick = onBackToTimer,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text("Back to Timer")
        }
    }
}

private fun Int?.orZero(): Int = this ?: 0

private fun formatTime(totalSeconds: Int): String =
    "%02d:%02d".format(totalSeconds / 60, totalSeconds % 60)

private fun findWinner(board: List<String>): String? {
    val winningLines = listOf(
        listOf(0, 1, 2),
        listOf(3, 4, 5),
        listOf(6, 7, 8),
        listOf(0, 3, 6),
        listOf(1, 4, 7),
        listOf(2, 5, 8),
        listOf(0, 4, 8),
        listOf(2, 4, 6)
    )

    for (line in winningLines) {
        val a = board[line[0]]
        val b = board[line[1]]
        val c = board[line[2]]
        if (a.isNotEmpty() && a == b && b == c) {
            return a
        }
    }

    return null
}

@Preview(showBackground = true)
@Composable
fun TimerPreview() {
    CPEN321ApplicationTheme {
        TimerScreen()
    }
}

