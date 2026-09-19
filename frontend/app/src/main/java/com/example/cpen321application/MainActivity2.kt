package com.example.cpen321application

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cpen321application.ui.theme.CPEN321ApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CPEN321ApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Column (
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize().padding(24.dp)
                    ) {
                        Greeting2(
                            apiBaseUrl = BuildConfig.API_BASE_URL,
                            modifier = Modifier.padding(innerPadding)
                        )
                        val displayName = intent.getStringExtra("displayName")
                        Text(
                            text = "Your Name: $displayName"
                        )
                        Greeting3(
                            apiBaseUrl = BuildConfig.API_BASE_URL,
                            modifier = Modifier.padding(innerPadding)
                        )
                        Text(
                            text = localIpAddress().toString()
                        )
                        Greeting4(
                            apiBaseUrl = BuildConfig.API_BASE_URL,
                            modifier = Modifier.padding(innerPadding)
                        )
                        localDateTime()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting2(apiBaseUrl: String, modifier: Modifier = Modifier) {
    var statusText by remember { mutableStateOf("Checking backend at $apiBaseUrl/devName...") }

    LaunchedEffect(apiBaseUrl) {
        statusText = fetchDevName(apiBaseUrl)
    }

    Text(
        text = statusText,
        modifier = modifier
    )
}
@Composable
fun Greeting3(apiBaseUrl: String, modifier: Modifier = Modifier) {
    var statusText by remember { mutableStateOf("Checking backend at $apiBaseUrl/serverIP...") }

    LaunchedEffect(apiBaseUrl) {
        statusText = fetchServerIP(apiBaseUrl)
    }

    Text(
        text = statusText,
        modifier = modifier
    )
}
@Composable
fun Greeting4(apiBaseUrl: String, modifier: Modifier = Modifier) {
    var statusText by remember { mutableStateOf("Checking backend at $apiBaseUrl/serverTime...") }

    LaunchedEffect(apiBaseUrl) {
        statusText = fetchServerTime(apiBaseUrl)
    }

    Text(
        text = statusText,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CPEN321ApplicationTheme {
        Greeting2("Android")
    }
}

@Composable
fun localDateTime() {
    Text(
        text = "Client Local Time: " + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss 'GMT'XXX"))
    )
}

@Composable
fun localIpAddress(): String? {
    var ip: String? = null
    val thread = Thread {
        try {
            val url = URL("https://api.ipify.org")
            val connection = url.openConnection()
            connection.setRequestProperty("User-Agent", "Mozilla/5.0") // Set a User-Agent to avoid HTTP 403 Forbidden error
            val inputStream = connection.getInputStream()
            val s = java.util.Scanner(inputStream, "UTF-8").useDelimiter("\\A")
            ip = s.next()
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    thread.start()
    return ip
}

private suspend fun fetchDevName(apiBaseUrl: String): String = withContext(Dispatchers.IO) {
    val healthUrl = "${apiBaseUrl.trimEnd('/')}/devName"
    try {
        val connection = (URL(healthUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 5_000
            readTimeout = 5_000
        }

        when (val code = connection.responseCode) {
            HttpURLConnection.HTTP_OK -> {
                val body = connection.inputStream.bufferedReader().use { it.readText() }
                "Developer Name: $body"
            }
            else -> {
                val errorBody = connection.errorStream?.bufferedReader()?.use { it.readText() }
                "Backend error ($healthUrl): HTTP $code${errorBody?.let { " — $it" } ?: ""}"
            }
        }
    } catch (e: Exception) {
        "Backend unreachable ($healthUrl): ${e.message ?: e.javaClass.simpleName}"
    }
}

private suspend fun fetchServerIP(apiBaseUrl: String): String = withContext(Dispatchers.IO) {
    val healthUrl = "${apiBaseUrl.trimEnd('/')}/serverIP"
    try {
        val connection = (URL(healthUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 5_000
            readTimeout = 5_000
        }

        when (val code = connection.responseCode) {
            HttpURLConnection.HTTP_OK -> {
                val body = connection.inputStream.bufferedReader().use { it.readText() }
                "Server IP: $body"
            }
            else -> {
                val errorBody = connection.errorStream?.bufferedReader()?.use { it.readText() }
                "Backend error ($healthUrl): HTTP $code${errorBody?.let { " — $it" } ?: ""}"
            }
        }
    } catch (e: Exception) {
        "Backend unreachable ($healthUrl): ${e.message ?: e.javaClass.simpleName}"
    }
}

private suspend fun fetchServerTime(apiBaseUrl: String): String = withContext(Dispatchers.IO) {
    val healthUrl = "${apiBaseUrl.trimEnd('/')}/serverTime"
    try {
        val connection = (URL(healthUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 5_000
            readTimeout = 5_000
        }

        when (val code = connection.responseCode) {
            HttpURLConnection.HTTP_OK -> {
                val body = connection.inputStream.bufferedReader().use { it.readText() }
                "Server Local Time: $body"
            }
            else -> {
                val errorBody = connection.errorStream?.bufferedReader()?.use { it.readText() }
                "Backend error ($healthUrl): HTTP $code${errorBody?.let { " — $it" } ?: ""}"
            }
        }
    } catch (e: Exception) {
        "Backend unreachable ($healthUrl): ${e.message ?: e.javaClass.simpleName}"
    }
}