package com.kouti.aichat.data.services

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import org.json.JSONObject
import org.json.JSONArray

class GeminiService(private val preferenceManager: PreferenceManager) {
    private val modelName = "gemma-4-26b-a4b-it"
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/"

    suspend fun sendMessage(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = preferenceManager.getApiKey() ?: return@withContext "Error: API Key not set. Please go to settings."
        
        try {
            val url = URL("${baseUrl}${modelName}:generateContent?key=${apiKey}")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonRequest = JSONObject().apply {
                put("contents", JSONArray().put(
                    JSONObject().apply {
                        put("parts", JSONArray().put(
                            JSONObject().apply { put("text", prompt) }
                        ))
                    }
                ))
            }

            OutputStreamWriter(connection.outputStream).use { it.write(jsonRequest.toString()) }

            if (connection.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val responseText = reader.readText()
                val jsonResponse = JSONObject(responseText)
                
                // Parsing the Gemini API response structure
                val candidates = jsonResponse.getJSONArray("candidates")
                val content = candidates.getJSONObject(0).getJSONObject("content")
                val parts = content.getJSONArray("parts")
                return@withContext parts.getJSONObject(0).getString("text")
            } else {
                val errorText = connection.errorStream?.bufferedReader()?.readText() ?: "Unknown error"
                return@withContext "API Error: ${connection.responseCode} - $errorText"
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "Exception during API call", e)
            return@withContext "Exception: ${e.localizedMessage}"
        }
    }
}
