package com.example.myapplication.data.repository

import com.example.myapplication.data.datasource.FraudJsonDataSource
import com.example.myapplication.domain.model.PhoneNumberInfo
import com.example.myapplication.domain.model.RiskLevel
import com.example.myapplication.domain.model.UserCallSettings
import com.example.myapplication.domain.repository.FraudRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant

class JsonFraudRepository(
    private val dataSource: FraudJsonDataSource
) : FraudRepository {

    private val history = MutableStateFlow<List<PhoneNumberInfo>>(emptyList())
    private var settings = UserCallSettings()

    private val fraudNumbers: List<PhoneNumberInfo> by lazy {
        dataSource.loadFraudNumbers().map { item ->
            PhoneNumberInfo(
                number = normalizeNumber(item.number),
                riskLevel = item.riskLevel.toRiskLevel(),
                reportsCount = item.reportsCount,
                category = item.category,
                lastReportedAt = item.lastReportedAt?.let { Instant.parse(it) }
            )
        }
    }

    override suspend fun getNumberInfo(number: String): PhoneNumberInfo? {
        val normalized = normalizeNumber(number)
        return fraudNumbers.firstOrNull { it.number == normalized }
    }

    override suspend fun saveSearch(info: PhoneNumberInfo) {
        history.value = listOf(info) + history.value
    }

    override fun observeHistory(): Flow<List<PhoneNumberInfo>> {
        return history.asStateFlow()
    }

    override suspend fun getUserSettings(): UserCallSettings {
        return settings
    }

    override suspend fun updateUserSettings(settings: UserCallSettings) {
        this.settings = settings
    }

    private fun String.toRiskLevel(): RiskLevel {
        return when (uppercase()) {
            "SAFE" -> RiskLevel.SAFE
            "SUSPECT" -> RiskLevel.SUSPECT
            "SPAM" -> RiskLevel.SPAM
            else -> RiskLevel.SAFE
        }
    }

    private fun normalizeNumber(number: String): String {
        return number.filter { it.isDigit() }
    }
}