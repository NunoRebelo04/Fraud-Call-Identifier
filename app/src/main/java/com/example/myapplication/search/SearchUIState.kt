package com.example.myapplication.search

import com.example.myapplication.domain.model.PhoneNumberInfo
import com.example.myapplication.domain.model.UserCallSettings

data class SearchUiState(
    val input: String = "",
    val isLoading: Boolean = false,
    val result: PhoneNumberInfo? = null,
    val error: String? = null,
    val history: List<PhoneNumberInfo> = emptyList(),
    val settings: UserCallSettings = UserCallSettings()
)