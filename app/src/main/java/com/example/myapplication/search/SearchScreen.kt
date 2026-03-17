package com.example.myapplication.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.model.PhoneNumberInfo
import com.example.myapplication.domain.model.RiskLevel
import java.time.Duration
import java.time.Instant
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.data.manager.FraudNotificationManager
import kotlinx.coroutines.flow.collect


fun formatRelativeDate(date: Instant): String {
    val now = Instant.now()
    val days = Duration.between(date, now).toDays()

    return when {
        days <= 0 -> "Hoje"
        days == 1L -> "Ontem"
        days < 7 -> "Há $days dias"
        else -> date.toString()
    }
}

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context =LocalContext.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

     LaunchedEffect(Unit) {
        viewModel.blockedCallEvents.collect {info ->
            FraudNotificationManager.showBlockedCallNotification(
                context=context,
                number=info.number,
                category=info.category
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = uiState.input,
                onValueChange = viewModel::onInputChange,
                label = { Text("Número de telefone") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.settings.blockSuspectCalls,
                    onCheckedChange = { viewModel.onBlockSuspectChanged(it) }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Bloquear números suspeitos")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.settings.blockSpamCalls,
                    onCheckedChange = { viewModel.onBlockSpamChanged(it) }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Bloquear números spam")
            }

            Button(
                onClick = { viewModel.validateNumber() },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {

                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(4.dp)
                    )
                } else {
                    Text("Validar número")
                }
            }

            uiState.result?.let { info ->
                ResultCard(info)
            }

            if (uiState.history.isNotEmpty()) {

                Text(
                    text = "Histórico de pesquisas",
                    style = MaterialTheme.typography.h6
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.history) { item ->
                        HistoryItem(info = item)
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultCard(info: PhoneNumberInfo) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {

        Text(
            text = info.number,
            style = MaterialTheme.typography.h6
        )

        Text(
            text = when (info.riskLevel) {
                RiskLevel.SAFE -> "Seguro"
                RiskLevel.SUSPECT -> "Suspeito"
                RiskLevel.SPAM -> "Spam confirmado"
            },
            style = MaterialTheme.typography.h5
        )

        info.reportsCount?.let {
            Text("Denúncias: $it")
        }

        info.category?.let {
            Text("Categoria: $it")
        }

        info.lastReportedAt?.let {
            Text("Última vez reportado: ${formatRelativeDate(it)}")
        }

        Text(
            text = "Seria bloqueado: ${if (info.shouldBlock) "Sim" else "Não"}"
        )
    }
}