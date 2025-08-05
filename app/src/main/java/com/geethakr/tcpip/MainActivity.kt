package com.geethakr.tcpip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.geethakr.tcpip.ui.theme.TCPIPTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TCPIPTheme {
                ClientApp()

            }
        }
    }
}

/**
 * The main composable for the Android client application.
 * It provides a UI for entering server details and sending a message.
 */
@Composable
fun ClientApp() {
    // State variables for the UI
    var ipAddress by remember { mutableStateOf("192.168.1.4") } // Replace with your desktop's IP
    val port = 8888
    var messageToSend by remember { mutableStateOf("Hello from Android!") }
    var connectionStatus by remember { mutableStateOf("Not connected") }
    var receivedMessage by remember { mutableStateOf("Waiting for messages...") }
    var clientSocket: Socket? by remember { mutableStateOf(null) }
    val coroutineScope = rememberCoroutineScope()
    var receiveJob: Job? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // TextField for the server's IP address
        TextField(
            value = ipAddress,
            onValueChange = { ipAddress = it },
            label = { Text("Server IP Address") },
            enabled = clientSocket == null
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Button to connect to the server
        Button(onClick = {
            connectionStatus = "Connecting..."
            // Launch a coroutine to handle the connection
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        val socket = Socket(ipAddress, port)
                        clientSocket = socket // Store the socket for later use
                        withContext(Dispatchers.Main) {
                            connectionStatus = "Connected to ${socket.inetAddress.hostAddress}"
                        }
                        // Launch a new coroutine to continuously listen for messages
                        receiveJob = launch(Dispatchers.IO) {
                            handleServerConnection(socket) { message ->
                                //withContext(Dispatchers.Main) {
                                    receivedMessage = message
                                //}
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            connectionStatus = "Error: ${e.message}"
                            e.printStackTrace()
                        }
                    }
                }
            }
        }, enabled = clientSocket == null) {
            Text("Connect")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Text field for the message to be sent
        TextField(
            value = messageToSend,
            onValueChange = { messageToSend = it },
            label = { Text("Message to send") },
            enabled = clientSocket != null
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Button to send a message
        Button(onClick = {
            // Launch a coroutine to send the message
            coroutineScope.launch(Dispatchers.IO) {
                clientSocket?.let { socket ->
                    sendMessage(socket, messageToSend)
                }
            }
        }, enabled = clientSocket != null && messageToSend.isNotBlank()) {
            Text("Send Message")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display the current connection status
        Text(
            text = "Connection Status: $connectionStatus",
            fontWeight = FontWeight.Bold,
            color = if (connectionStatus.startsWith("Error")) Color.Red else Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))
        // Display the received message from the server
        Text(
            text = "Received from server: $receivedMessage",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button to disconnect
        Button(onClick = {
            coroutineScope.launch {
                clientSocket?.close()
                receiveJob?.cancel()
                clientSocket = null
                receiveJob = null
                connectionStatus = "Disconnected"
            }
        }, enabled = clientSocket != null) {
            Text("Disconnect")
        }
    }
}

/**
 * Handles the communication loop with the server.
 * This function continuously reads messages and provides them to a callback.
 *
 * @param socket The client socket.
 * @param onMessageReceived A callback function to handle received messages.
 */
suspend fun handleServerConnection(socket: Socket, onMessageReceived: (String) -> Unit) {
    withContext(Dispatchers.IO) {
        try {
            val reader = BufferedReader(InputStreamReader(socket.inputStream))
            while (isActive) {
                val message = reader.readLine()
                if (message != null) {
                    onMessageReceived(message)
                } else {
                    break
                }
            }
        } catch (e: Exception) {
            println("Error receiving message: ${e.message}")
            e.printStackTrace()
        }
    }
}

/**
 * Sends a message to the connected server.
 *
 * @param socket The server socket to send the message to.
 * @param message The message string to be sent.
 */
suspend fun sendMessage(socket: Socket, message: String) {
    withContext(Dispatchers.IO) {
        try {
            val writer = PrintWriter(socket.getOutputStream(), true)
            writer.println(message)
        } catch (e: Exception) {
            println("Error sending message: ${e.message}")
            e.printStackTrace()
        }
    }
}
