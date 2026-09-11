package com.example.cpen321application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cpen321application.ui.theme.CPEN321ApplicationTheme
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

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
                        Greeting5(
                            name = "Android",
                            modifier = Modifier.padding(innerPadding)
                        )
                        val client = OkHttpClient()
                        val request = Request.Builder()
                            .url("ws://10.0.2.2:8080")
                            .build()
                        val listener = EchoWebSocketListener()
                        val webSocket = client.newWebSocket(request, listener)
                        Row {
                            Button(onClick = { webSocket.send("Hello") }) { Text("Send Message") }
                        }
                    }
                }

            }
        }
    }

    class EchoWebSocketListener : WebSocketListener() {
        var data: String = ""

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            //data = bytes
            data = bytes.toString()
        }

        fun log_data(): String{
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