package com.example.myapplication.data.datasource

import android.content.Context
import com.example.myapplication.domain.model.FraudNumberJson
import org.json.JSONArray

class FraudJsonDataSource(
    private val context: Context
) {
    fun loadFraudNumbers(): List<FraudNumberJson> {
        val jsonString = context.assets
            .open("fraud_numbers.json")
            .bufferedReader()
            .use { it.readText() }

        val jsonArray = JSONArray(jsonString)

        return buildList {
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)

                add(
                    FraudNumberJson(
                        number = item.getString("number"),
                        riskLevel = item.getString("riskLevel"),
                        reportsCount = if (item.has("reportsCount")) item.optInt("reportsCount") else null,
                        category = if (item.has("category")) item.optString("category") else null,
                        lastReportedAt = if (item.has("lastReportedAt")) item.optString("lastReportedAt") else null
                    )
                )
            }
        }
    }
}