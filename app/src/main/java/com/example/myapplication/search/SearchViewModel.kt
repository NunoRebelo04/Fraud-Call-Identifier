package com.example.myapplication.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.PhoneNumberInfo
import com.example.myapplication.domain.usecase.AnalyzePhoneNumberUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import repository.InMemoryFraudRepository
import com.example.myapplication.domain.model.RiskLevel
import com.example.myapplication.domain.model.UserCallSettings

class SearchViewModel : ViewModel() {

    private val repository = InMemoryFraudRepository()
    private val analyzePhoneNumberUseCase = AnalyzePhoneNumberUseCase(repository)

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _blockedCallEvents = MutableSharedFlow<PhoneNumberInfo>()
    val blockedCallEvents = _blockedCallEvents.asSharedFlow()

    init {
        observeHistory()
        loadSettings()
    }

    fun onInputChange(value: String) {
        _uiState.update { current ->
            current.copy(input = value)
        }
    }

    private fun recalculateShouldBlock(
        info: PhoneNumberInfo,
        settings: UserCallSettings
    ): PhoneNumberInfo {
        val shouldBlock = when (info.riskLevel) {
            RiskLevel.SAFE -> false
            RiskLevel.SUSPECT -> settings.blockSuspectCalls
            RiskLevel.SPAM -> settings.blockSpamCalls
        }

        return info.copy(shouldBlock = shouldBlock)
    }

    fun validateNumber() {
        viewModelScope.launch {
            val cleaned = _uiState.value.input.filter { it.isDigit() }

            if (cleaned.length !in 9..15) {
                _uiState.update { current ->
                    current.copy(error = "Número inválido")
                }
                return@launch
            }

            _uiState.update { current ->
                current.copy(isLoading = true, error = null)
            }

            val settings = repository.getUserSettings()
            val info = analyzePhoneNumberUseCase(cleaned, settings)

            repository.saveSearch(info)

            if (info.shouldBlock) {
                _blockedCallEvents.emit(info)
            }

            _uiState.update { current ->
                current.copy(
                    isLoading = false,
                    result = info
                )
            }
        }
    }

    fun onBlockSuspectChanged(enabled: Boolean) {
            viewModelScope.launch {
            val newSettings = _uiState.value.settings.copy(
                blockSuspectCalls = enabled
            )

            repository.updateUserSettings(newSettings)

            val updatedResult = _uiState.value.result?.let { currentResult ->
                recalculateShouldBlock(currentResult, newSettings)
            }

            _uiState.update { current ->
                current.copy(
                    settings = newSettings,
                    result = updatedResult
                )
            }
        }
    }

    fun onBlockSpamChanged(enabled: Boolean) {
            viewModelScope.launch {
            val newSettings = _uiState.value.settings.copy(
                blockSpamCalls = enabled
            )

            repository.updateUserSettings(newSettings)

            val updatedResult = _uiState.value.result?.let { currentResult ->
                recalculateShouldBlock(currentResult, newSettings)
            }

            _uiState.update { current ->
                current.copy(
                    settings = newSettings,
                    result = updatedResult
                )
            }
        }   
    }

    fun clearError() {
        _uiState.update { current ->
            current.copy(error = null)
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            repository.observeHistory().collect { history ->
                _uiState.update { current ->
                    current.copy(history = history)
                }
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val settings = repository.getUserSettings()
            _uiState.update { current ->
                current.copy(settings = settings)
            }
        }
    }
}