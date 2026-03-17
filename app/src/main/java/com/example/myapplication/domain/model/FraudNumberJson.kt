package com.example.myapplication.domain.model

data class FraudNumberJson(
    val number: String,
    val riskLevel: String,
    val reportsCount: Int? = null,
    val category: String? = null,
    val lastReportedAt: String? = null
)