package com.example.cpen321application

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.Canvas
import androidx.compose.material3.Button
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.cpen321application.ui.theme.CPEN321ApplicationTheme
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject


class MainActivity3 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CPEN321ApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val client = remember { OkHttpClient() }
                        val listener = remember { EchoWebSocketListener() }
                        val webSocket = remember {
                            client.newWebSocket(
                                Request.Builder().url("ws://${BuildConfig.API_BASE_URL.removePrefix("http://")}").build(),
                                listener
                            )
                        }
                        var grid by remember {
                            mutableStateOf(List(16 * 16) { Color.White })
                        }

                        LaunchedEffect(listener) {
                            while (true) {
                                val pixel = listener.get_data()
                                if (pixel.has("x") && pixel.has("y") && pixel.has("color")) {
                                    val x = pixel.optInt("x", -1)
                                    val y = pixel.optInt("y", -1)
                                    if (x in 0 until 16 && y in 0 until 16) {
                                        try {
                                            val color = Color(
                                                android.graphics.Color.parseColor(
                                                    pixel.getString("color")
                                                )
                                            )
                                            grid = grid.toMutableList().also {
                                                it[y * 16 + x] = color
                                            }
                                        } catch (_: IllegalArgumentException) {
                                            // Ignore malformed color values.
                                        }
                                    }
                                }
                                delay(10L)
                            }
                        }

                        Greeting5(
                            name = "Android",
                            modifier = Modifier.padding(innerPadding)
                        )
                        Canvas(
                            modifier = Modifier
                                .padding(16.dp)
                                .then(Modifier.fillMaxSize())
                        ) {
                            val cellWidth = size.width / 16f
                            val cellHeight = size.height / 16f
                            grid.forEachIndexed { index, color ->
                                drawRect(
                                    color = color,
                                    topLeft = Offset(
                                        (index % 16) * cellWidth,
                                        (index / 16) * cellHeight
                                    ),
                                    size = Size(cellWidth, cellHeight)
                                )
                            }
                        }
//                        Row {
//                            Button(onClick = { webSocket.send("Hello") }) { Text("Send Message") }
//                        }
//                        Row {
//                            Button(onClick = { Log.i(TAG, listener.get_data().toString())}) { Text("Log Message") }
//                        }
//                        Row {
//                            Button(onClick = { Log.i(TAG, "test")}) { Text("test") }
//                        }
                    }
                }
            }
        }
    }

    class EchoWebSocketListener : WebSocketListener() {
        var data: JSONObject = JSONObject()

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            val message = bytes.utf8()
            data = JSONObject(message)
            //Log.i(TAG, data.toString())
        }

        fun get_data(): JSONObject{
            return data
        }
    }
}

@Composable
fun Greeting5(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    CPEN321ApplicationTheme {
        Greeting5("Android")
    }
}