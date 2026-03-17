package com.example.myapplication.ui.extensions

import androidx.compose.ui.graphics.Color
import com.example.myapplication.domain.model.RiskLevel
import com.example.myapplication.ui.theme.GreenSafe
import com.example.myapplication.ui.theme.GreenSafeBg
import com.example.myapplication.ui.theme.RedSpam
import com.example.myapplication.ui.theme.RedSpamBg
import com.example.myapplication.ui.theme.YellowSuspect
import com.example.myapplication.ui.theme.YellowSuspectBg

fun RiskLevel.label(): String = when (this) {
    RiskLevel.SAFE -> "Seguro"
    RiskLevel.SUSPECT -> "Suspeito"
    RiskLevel.SPAM -> "Spam"
}

fun RiskLevel.color(): Color = when (this) {
    RiskLevel.SAFE -> GreenSafe
    RiskLevel.SUSPECT -> YellowSuspect
    RiskLevel.SPAM -> RedSpam
}

fun RiskLevel.backgroundColor(): Color = when (this) {
    RiskLevel.SAFE -> GreenSafeBg
    RiskLevel.SUSPECT -> YellowSuspectBg
    RiskLevel.SPAM -> RedSpamBg
}