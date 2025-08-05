# My Project
This is a simple Android application using Jetpack Compose that will act as a TCP IP client. It will connect to the desktop TCP IP server and send/receive a message continously.

#How to Run

#Find your Desktop IP Address: 
On your desktop, open a terminal or command prompt and type ipconfig (Windows) or ifconfig / ip addr (macOS/Linux) to find your local IP address. It will likely be in the format 192.168.x.x.

#Start the Desktop Server: 
Run the Main.kt file in your Jetpack Compose Desktop project. Click the "Start Server" button.

#Update Android Client: 
In your Android project's MainActivity.kt, replace the placeholder ipAddress with your desktop's actual IP address.

#Run the Android App: 
Deploy the Android application to a device or emulator on the same network as your desktop.

#Send a Message: 
On the Android app, enter a message and press the "Connect and Send Message" button.

You should see the message appear in the log on the desktop application's console and in the text field on the server UI.
