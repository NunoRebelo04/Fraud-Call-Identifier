package repository

import com.example.myapplication.domain.model.PhoneNumberInfo
import com.example.myapplication.domain.model.RiskLevel
import com.example.myapplication.domain.model.UserCallSettings
import com.example.myapplication.domain.repository.FraudRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant

class InMemoryFraudRepository : FraudRepository {

    private val historyFlow = MutableStateFlow<List<PhoneNumberInfo>>(emptyList())
    private var settings: UserCallSettings = UserCallSettings()

    private val suspiciousNumbers = listOf(
        PhoneNumberInfo(
            number = "918876579",
            riskLevel = RiskLevel.SPAM,
            reportsCount = 42,
            category = "Fraude Bancária",
            lastReportedAt = Instant.parse("2026-03-10T10:15:30Z")
        ),
        PhoneNumberInfo(
            number = "939083472",
            riskLevel = RiskLevel.SUSPECT,
            reportsCount = 9,
            category = "Oferta Enganosa",
            lastReportedAt = Instant.parse("2026-03-14T10:15:30Z")
        ),
        PhoneNumberInfo(
            number = "960144155",
            riskLevel = RiskLevel.SUSPECT,
            reportsCount = 23,
            category = "Telemarketing Agressivo",
            lastReportedAt = Instant.parse("2026-02-27T18:17:20Z")
        )
    )

    override suspend fun getNumberInfo(number: String): PhoneNumberInfo? {
        return suspiciousNumbers.firstOrNull { it.number == number }
    }

    override suspend fun saveSearch(info: PhoneNumberInfo) {
        historyFlow.value = (listOf(info) + historyFlow.value).take(10)
    }

    override fun observeHistory(): Flow<List<PhoneNumberInfo>> {
        return historyFlow.asStateFlow()
    }

    override suspend fun getUserSettings(): UserCallSettings {
        return settings
    }

    override suspend fun updateUserSettings(settings: UserCallSettings) {
        this.settings = settings
    }
}