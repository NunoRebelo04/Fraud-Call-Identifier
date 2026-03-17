package com.example.myapplication.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.PhoneNumberInfo

@Composable
fun HistoryItem(info: PhoneNumberInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = info.number,
            style = MaterialTheme.typography.subtitle1
        )
        Text(
            text = "Risco: ${info.riskLevel}",
            style = MaterialTheme.typography.body2
        )
    }
}