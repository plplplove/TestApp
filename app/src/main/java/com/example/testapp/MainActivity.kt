package com.example.testapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        if (prefs.contains("server_result")) {
            val result = prefs.getBoolean("server_result", false)
            openNextScreen(result)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = getServerResult()
                    prefs.edit().putBoolean("server_result", response).apply()
                    withContext(Dispatchers.Main) {
                        openNextScreen(response)
                    }
                } catch (e: Exception) {
                    prefs.edit().putBoolean("server_result", false).apply()
                    withContext(Dispatchers.Main) {
                        openNextScreen(false)
                    }
                }
            }
        }
    }

    private fun openNextScreen(result: Boolean) {
        val intent = if (result) {
            Intent(this, WebViewActivity::class.java)
        } else {
            Intent(this, GameMenuActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    private fun getServerResult(): Boolean {
        val url = URL("https://run.mocky.io/v3/c6af0421-ebe1-47f4-89d6-d1154b407f04")
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        connection.requestMethod = "GET"
        val response = connection.inputStream.bufferedReader().readText().trim()
        connection.disconnect()
        return response == "true"
    }
}