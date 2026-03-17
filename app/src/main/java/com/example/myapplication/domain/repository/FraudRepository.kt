package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.PhoneNumberInfo
import com.example.myapplication.domain.model.UserCallSettings
import kotlinx.coroutines.flow.Flow

interface FraudRepository {
    suspend fun getNumberInfo(number: String): PhoneNumberInfo?
    suspend fun saveSearch(info: PhoneNumberInfo)
    fun observeHistory(): Flow<List<PhoneNumberInfo>>
    suspend fun getUserSettings(): UserCallSettings
    suspend fun updateUserSettings(settings: UserCallSettings)
}