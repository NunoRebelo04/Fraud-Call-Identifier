package com.example.myapplication.telecom

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import com.example.myapplication.data.datasource.FraudJsonDataSource
import com.example.myapplication.data.manager.FraudNotificationManager
import com.example.myapplication.data.repository.JsonFraudRepository
import com.example.myapplication.domain.repository.FraudRepository
import com.example.myapplication.domain.usecase.AnalyzePhoneNumberUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class FraudCallScreeningService : CallScreeningService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    private val repository: FraudRepository by lazy {
        AppContainer.provideRepository()
    }

    private val analyzePhoneNumberUseCase: AnalyzePhoneNumberUseCase by lazy {
        AppContainer.provideAnalyzePhoneNumberUseCase()
    }



    override fun onScreenCall(callDetails: Call.Details) {


        val rawNumber = callDetails.handle?.schemeSpecificPart.orEmpty()

        serviceScope.launch {
            val cleanedNumber = rawNumber.filter { it.isDigit() }
            val settings = repository.getUserSettings()
            val info = analyzePhoneNumberUseCase(cleanedNumber, settings)

            val response = if (info.shouldBlock) {
                CallResponse.Builder()
                    .setDisallowCall(true)
                    .setRejectCall(true)
                    .setSilenceCall(true)
                    .setSkipCallLog(false)
                    .setSkipNotification(false)
                    .build()
            } else {
                CallResponse.Builder()
                    .setDisallowCall(false)
                    .setRejectCall(false)
                    .setSilenceCall(false)
                    .setSkipCallLog(false)
                    .setSkipNotification(false)
                    .build()
            }

            respondToCall(callDetails, response)

            if (info.shouldBlock) {
                showFraudNotification(
                    number = info.number,
                    category = info.category
                )
            }
        }
    }

    private fun showFraudNotification(
        number: String,
        category: String?
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS

            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        FraudNotificationManager.createChannel(applicationContext)
        FraudNotificationManager.showBlockedCallNotification(
            context = applicationContext,
            number = number,
            category = category
        )
    }
}