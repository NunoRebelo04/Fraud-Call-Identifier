package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.PhoneNumberInfo
import com.example.myapplication.domain.model.RiskLevel
import com.example.myapplication.domain.model.UserCallSettings
import com.example.myapplication.domain.repository.FraudRepository

class AnalyzePhoneNumberUseCase(
    private val repository: FraudRepository
) {

    suspend operator fun invoke(
        number: String,
        settings: UserCallSettings
    ): PhoneNumberInfo {
        val found = repository.getNumberInfo(number)

        val baseInfo = found ?: PhoneNumberInfo(
            number = number,
            riskLevel = RiskLevel.SAFE,
            reportsCount = null,
            category = null,
            lastReportedAt = null
        )

        val shouldBlock = when (baseInfo.riskLevel) {
            RiskLevel.SAFE -> false
            RiskLevel.SUSPECT -> settings.blockSuspectCalls
            RiskLevel.SPAM -> settings.blockSpamCalls
        }

        return baseInfo.copy(shouldBlock = shouldBlock)
    }
}