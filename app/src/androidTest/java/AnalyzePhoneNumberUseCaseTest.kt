import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.myapplication.data.datasource.FraudJsonDataSource
import com.example.myapplication.data.repository.JsonFraudRepository
import com.example.myapplication.domain.model.RiskLevel
import com.example.myapplication.domain.model.UserCallSettings
import com.example.myapplication.domain.usecase.AnalyzePhoneNumberUseCase
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class AnalyzePhoneNumberUseCaseTest{
    private lateinit var repository: JsonFraudRepository
    private lateinit var useCase: AnalyzePhoneNumberUseCase

    @Before
    fun setup(){
        val context= ApplicationProvider.getApplicationContext<Context>()
        val dataSource= FraudJsonDataSource(context)
        repository= JsonFraudRepository(dataSource)
        useCase= AnalyzePhoneNumberUseCase(repository)
    }

    @Test
    fun shouldReturnSpamForKnowSpamNumber(){
        val settings= UserCallSettings(
            blockSpamCalls = true,
            blockSuspectCalls = false
        )

        val result= runBlocking {
            useCase("918876579", settings)
        }

        assertEquals(RiskLevel.SPAM, result.riskLevel)
        TestCase.assertTrue(result.shouldBlock)
    }

    @Test
    fun shouldReturnSuspectForKnowSuspectNumber(){
        val settings= UserCallSettings(
            blockSpamCalls = false,
            blockSuspectCalls = true
        )

        val result= runBlocking {
            useCase("939083472", settings)
        }

        assertEquals(RiskLevel.SUSPECT, result.riskLevel)
        TestCase.assertTrue(result.shouldBlock)
    }

     @Test
    fun shouldReturnSuspectAndNotBlockWhenSettingisDisabled(){
        val settings= UserCallSettings(
            blockSpamCalls = true,
            blockSuspectCalls = false
        )

        val result= runBlocking {
            useCase("939083472", settings)
        }

         assertEquals(RiskLevel.SUSPECT, result.riskLevel)
         TestCase.assertFalse(result.shouldBlock)
    }

     @Test
    fun shouldReturnSafeForUnknownNumber(){
        val settings= UserCallSettings(
            blockSpamCalls = true,
            blockSuspectCalls = true
        )

        val result= runBlocking {
            useCase("9111111111", settings)
        }

         assertEquals(RiskLevel.SAFE, result.riskLevel)
         TestCase.assertFalse(result.shouldBlock)
    }

}