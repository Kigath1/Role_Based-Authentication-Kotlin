package com.example.Roomdb.viewmodel.common.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomdb.data.local.SecureTokenDataStore
import com.example.Roomdb.data.model.employer.Message
import com.example.Roomdb.domain.usecases.employer.GetConversationUseCase
import com.example.Roomdb.domain.usecases.employer.MarkMessageAsReadUseCase
import com.example.Roomdb.domain.usecases.employer.SendMessageUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

private const val POLL_INTERVAL_MS = 5000L

class ChatViewModel(
    private val getConversationUseCase: GetConversationUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val markMessageAsReadUseCase: MarkMessageAsReadUseCase,
    private val secureStore: SecureTokenDataStore,
    val recipientId: String,
    val recipientName: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    init {
        viewModelScope.launch {
            val userId = secureStore.getUserIdOnce() ?: return@launch
            _uiState.update { it.copy(currentUserId = userId) }
            refreshConversation(userId, silent = false)
            startPolling(userId)
        }
    }

    // Polls while the chat is open so the recipient sees new messages without
    // manually leaving and reopening the thread. Cancelled automatically when
    // this ViewModel is cleared (viewModelScope), since it's created per-entry
    // and destroyed when the user backs out of Chat.
    private fun startPolling(userId: String) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(POLL_INTERVAL_MS)
                refreshConversation(userId, silent = true)
            }
        }
    }

    private suspend fun refreshConversation(userId: String, silent: Boolean) {
        if (!silent) _uiState.update { it.copy(isLoading = true, error = null) }

        val result = getConversationUseCase(userId, recipientId)

        result.onSuccess { list ->
            // API returns newest-first; reverse so oldest is at the top
            val ordered = list.reversed()
            _uiState.update { it.copy(isLoading = false, messages = ordered) }
            markUnreadReceivedMessagesAsRead(ordered, userId)
        }.onFailure { e ->
            // Silent (polling) failures are swallowed on purpose — surfacing a
            // transient network blip every 5s as a visible error would be worse
            // than just trying again on the next poll.
            if (!silent) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // The screen has no per-message visibility tracking, so this treats "the
    // thread is open and loaded" as "the user has seen every message in it" —
    // marks every unread message addressed to them as read. Good enough for a
    // single always-fully-visible-on-screen conversation; would need real
    // viewport tracking if the thread ever gets long enough to need scrolling
    // past unread messages without seeing them.
    private fun markUnreadReceivedMessagesAsRead(messages: List<Message>, currentUserId: String) {
        val unread = messages.filter { it.receiverId == currentUserId && !it.isRead }
        if (unread.isEmpty()) return

        viewModelScope.launch {
            unread.forEach { msg ->
                markMessageAsReadUseCase(msg.id).onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            messages = state.messages.map { m ->
                                if (m.id == msg.id) m.copy(isRead = true) else m
                            }
                        )
                    }
                }
                // Failure is silently ignored — worst case, it stays unread and
                // gets retried on the next poll cycle.
            }
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

    override fun onCleared() {
        pollingJob?.cancel()
        super.onCleared()
    }
}