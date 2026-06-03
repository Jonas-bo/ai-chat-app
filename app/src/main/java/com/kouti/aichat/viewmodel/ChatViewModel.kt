package com.kouti.aichat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kouti.aichat.data.models.Message
import com.kouti.aichat.data.services.GeminiService
import com.kouti.aichat.data.services.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val preferenceManager = PreferenceManager(application)
    private val geminiService = GeminiService(preferenceManager)

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = Message(text = text, isUser = true)
        _messages.value = _messages.value + userMessage

        viewModelScope.launch {
            _isLoading.value = true
            val response = geminiService.sendMessage(text)
            val aiMessage = Message(text = response, isUser = false)
            _messages.value = _messages.value + aiMessage
            _isLoading.value = false
        }
    }

    fun updateApiKey(newKey: String) {
        preferenceManager.saveApiKey(newKey)
    }

    fun getApiKey(): String {
        return preferenceManager.getApiKey() ?: ""
    }
}
