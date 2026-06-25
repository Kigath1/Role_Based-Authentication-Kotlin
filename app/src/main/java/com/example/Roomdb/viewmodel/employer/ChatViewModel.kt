package com.example.Roomdb.viewmodel.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomdb.data.local.SecureTokenDataStore
import com.example.Roomdb.data.model.employer.Message
import com.example.Roomdb.domain.usecases.employer.GetConversationUseCase
import com.example.Roomdb.domain.usecases.employer.SendMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val inputText: String = "",
    val isSending: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUserId: String = ""
)

class ChatViewModel(
    private val getConversationUseCase: GetConversationUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val secureStore: SecureTokenDataStore,
    val recipientId: String,
    val recipientName: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = secureStore.getUserIdOnce() ?: return@launch
            _uiState.update { it.copy(currentUserId = userId) }
            loadConversation(userId)
        }
    }

    private suspend fun loadConversation(userId: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        val result = getConversationUseCase(userId, recipientId)
        _uiState.update {
            it.copy(
                isLoading = false,
                // API returns newest-first; reverse so oldest is at the top
                messages = result.getOrElse { emptyList() }.reversed(),
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank()) return
        val senderId = _uiState.value.currentUserId

        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true, inputText = "") }

            val result = sendMessageUseCase(senderId, recipientId, text)

            result.onSuccess { sent ->
                // Append confirmed message from server to the list
                _uiState.update { state ->
                    state.copy(
                        isSending = false,
                        messages = state.messages + sent
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(isSending = false, error = e.message)
                }
            }
        }
    }
}