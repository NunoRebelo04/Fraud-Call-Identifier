package com.example.myapplication.telecom


import android.content.Context
import com.example.myapplication.data.datasource.FraudJsonDataSource
import com.example.myapplication.data.repository.JsonFraudRepository
import com.example.myapplication.domain.repository.FraudRepository
import com.example.myapplication.domain.usecase.AnalyzePhoneNumberUseCase

object AppContainer {

    private lateinit var repositoryInstance: FraudRepository
    private lateinit var analyzePhoneNumberUseCaseInstance: AnalyzePhoneNumberUseCase

    fun init(context: Context) {
        val appContext = context.applicationContext

        val dataSource = FraudJsonDataSource(appContext)
        repositoryInstance = JsonFraudRepository(dataSource)
        analyzePhoneNumberUseCaseInstance = AnalyzePhoneNumberUseCase(repositoryInstance)
    }

    fun provideRepository(): FraudRepository {
        return repositoryInstance
    }

    fun provideAnalyzePhoneNumberUseCase(): AnalyzePhoneNumberUseCase {
        return analyzePhoneNumberUseCaseInstance
    }
}