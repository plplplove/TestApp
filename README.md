# TestApp - Dual Mode Application

## Overview
TestApp is a versatile Android application with dual functionality:
1. **Web Mode**: Displays content in a WebView when server configuration indicates
2. **Game Mode**: Presents an Emoji Match game when web mode is disabled

The application checks with a remote server on startup to determine which mode to activate.

## Application Architecture

### Server Check Mechanism
On startup, the application queries a configurable server endpoint to determine operation mode:
- If the server returns "true", the WebView mode is activated
- If the server returns "false", the Game mode is activated

This configuration is cached in SharedPreferences to minimize server requests.

### Web Mode
When activated, the WebView mode:
- Loads a full-screen web browser within the app
- Supports JavaScript execution
- Accepts third-party cookies
- Handles back navigation within the web content
- Preserves web state during configuration changes

### Game Mode
The Emoji Match Game features:
- Simple gameplay requiring pattern matching
- 60-second timer
- Lives system (3 lives)
- Score tracking with persistent high scores
- Animated transitions and interactions
- Custom animated background with moving clouds

## Technical Requirements
- Android 6.0 (API level 23) or higher
- ViewBinding for UI element handling
- Kotlin Coroutines for asynchronous operations
- Internet connection for initial mode determination

## Installation and Setup
1. Clone the repository
2. Open the project in Android Studio
3. Ensure ViewBinding is enabled in build.gradle (Module):
4. Build and run the application on an emulator or physical device

## Configuration Options

### Server Endpoint Configuration
To change the server endpoint that determines the application mode, modify the `getServerResult()` function in `MainActivity.kt`:

```kotlin
    private fun getServerResult(): Boolean {
        val url = URL("https://") // Change this link to your server
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        connection.requestMethod = "GET"
        val response = connection.inputStream.bufferedReader().readText().trim()
        connection.disconnect()
        return response == "true"
    }