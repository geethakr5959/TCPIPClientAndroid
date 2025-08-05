# About
This is a simple Android application using Jetpack Compose that will act as a TCP IP client. It will connect to the desktop TCP IP server and send/receive a message continously.

# How to Run ??<br>

# Find your Desktop IP Address: <br>
On your desktop, open a terminal or command prompt and type ipconfig (Windows) or ifconfig / ip addr (macOS/Linux) to find your local IP address. It will likely be in the format 192.168.x.x.<br>

# Start the Desktop Server: <br>
Run the Main.kt file in your Jetpack Compose Desktop project. Click the "Start Server" button.<br>

<img width="843" height="521" alt="Ready for connection" src="https://github.com/user-attachments/assets/5df1de5c-c01a-46a7-aa9d-d9d16d136593" />

# Update Android Client: <br>
In your Android project's MainActivity.kt, replace the placeholder ipAddress with your desktop's actual IP address.<br>

# Run the Android App: <br> 
Deploy the Android application to a device or emulator on the same network as your desktop.<br>

# Send a Message: <br>
On the Android app, enter a message and press the "Connect and Send Message" button.<br>

<img width="843" height="522" alt="ConnectedToServer" src="https://github.com/user-attachments/assets/494eb633-0316-4cfb-b5d9-f61456cfb5c0" />


You should see the message appear in the log on the desktop application's console and in the text field on the server UI.<br>

