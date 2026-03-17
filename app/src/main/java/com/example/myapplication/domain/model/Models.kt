package com.example.myapplication.domain.model

import java.time.Instant

enum class RiskLevel {
    SAFE,
    SUSPECT,
    SPAM
}

data class PhoneNumberInfo(
    val number: String,
    val riskLevel: RiskLevel,
    val reportsCount: Int? = null,
    val category: String? = null,
    val lastReportedAt: Instant? = null,
    val shouldBlock : Boolean = false
)

data class UserCallSettings(
    val blockSpamCalls: Boolean = true,
    val blockSuspectCalls: Boolean = false
)

