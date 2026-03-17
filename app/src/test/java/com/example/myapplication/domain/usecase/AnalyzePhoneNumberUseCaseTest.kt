package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.RiskLevel
import com.example.myapplication.domain.model.UserCallSettings
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import repository.InMemoryFraudRepository
import kotlin.test.assertEquals

class AnalyzePhoneNumberUseCaseTest{
    private lateinit var repository: InMemoryFraudRepository
    private lateinit var useCase: AnalyzePhoneNumberUseCase

    @Before
    fun setup(){
        repository= InMemoryFraudRepository()
        useCase=AnalyzePhoneNumberUseCase(repository)
    }

    @Test
    fun shouldReturnSpamForKnowSpamNumber(){
        val settings= UserCallSettings(
            blockSpamCalls = true,
            blockSuspectCalls = false
        )

        val result= runBlocking{
            useCase("918876579",settings)
        }

        assertEquals(RiskLevel.SPAM,result.riskLevel)
        assertTrue(result.shouldBlock)
    }

    @Test
    fun shouldReturnSuspectForKnowSuspectNumber(){
        val settings= UserCallSettings(
            blockSpamCalls = false,
            blockSuspectCalls = true
        )

        val result= runBlocking{
            useCase("939083472",settings)
        }

        assertEquals(RiskLevel.SUSPECT,result.riskLevel)
        assertTrue(result.shouldBlock)
    }

     @Test
    fun shouldReturnSuspectAndNotBlockWhenSettingisDisabled(){
        val settings= UserCallSettings(
            blockSpamCalls = true,
            blockSuspectCalls = false
        )

        val result= runBlocking{
            useCase("939083472",settings)
        }

        assertEquals(RiskLevel.SUSPECT,result.riskLevel)
        assertFalse(result.shouldBlock)
    }

     @Test
    fun shouldReturnSafeForUnknownNumber(){
        val settings= UserCallSettings(
            blockSpamCalls = true,
            blockSuspectCalls = true
        )

        val result= runBlocking{
            useCase("9111111111",settings)
        }

        assertEquals(RiskLevel.SAFE,result.riskLevel)
        assertFalse(result.shouldBlock)
    }

}