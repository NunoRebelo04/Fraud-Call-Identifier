package com.example.myapplication.search

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.data.manager.FraudNotificationManager
import com.example.myapplication.domain.model.PhoneNumberInfo
import com.example.myapplication.ui.extensions.backgroundColor
import com.example.myapplication.ui.extensions.color
import com.example.myapplication.ui.extensions.label
import com.example.myapplication.ui.theme.TextPrimary
import com.example.myapplication.ui.theme.TextSecondary
import java.time.Duration
import java.time.Instant

fun formatRelativeDate(date: Instant): String {
    val now = Instant.now()
    val days = Duration.between(date, now).toDays()

    return when {
        days <= 0 -> "Hoje"
        days == 1L -> "Ontem"
        days < 7 -> "Há $days dias"
        else -> date.toString().take(10)
    }
}



@SuppressLint("MissingPermission")
@Composable
fun SearchScreen(
    viewModel: SearchViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.blockedCallEvents.collect { info ->
            FraudNotificationManager.showBlockedCallNotification(
                context = context,
                number = info.number,
                category = info.category
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fraud Call Identifier") }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Pesquisar número",
                            style = MaterialTheme.typography.h6
                        )

                        OutlinedTextField(
                            value = uiState.input,
                            onValueChange = { value ->
                                viewModel.onInputChange(value.filter { it.isDigit() })
                            },
                            label = { Text("Número de telefone") },
                            placeholder = { Text("Ex: 912345678") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = { viewModel.validateNumber() }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = { viewModel.validateNumber() },
                            enabled = !uiState.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("A validar...")
                            } else {
                                Text("Validar número")
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Preferências",
                            style = MaterialTheme.typography.h6
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Switch(
                                checked = uiState.settings.useDarkMode,
                                onCheckedChange = viewModel::onDarkModeChanged
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Ativar Dark Mode",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Divider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Switch(
                                checked = uiState.settings.blockSuspectCalls,
                                onCheckedChange = viewModel::onBlockSuspectChanged
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Bloquear números suspeitos",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Divider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Switch(
                                checked = uiState.settings.blockSpamCalls,
                                onCheckedChange = viewModel::onBlockSpamChanged
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Bloquear números spam",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Resultado",
                    style = MaterialTheme.typography.h6
                )
            }

            item {
                uiState.result?.let { info ->
                    ResultCard(info = info)
                } ?: Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 2.dp,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Sem resultado ainda",
                            style = MaterialTheme.typography.subtitle1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Introduz um número de telefone para validares o risco.",
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Histórico de pesquisas",
                    style = MaterialTheme.typography.h6
                )
            }

            if (uiState.history.isNotEmpty()) {
                items(uiState.history) { item ->
                    HistoryItem(info = item)
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 2.dp,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Ainda não existem pesquisas guardadas.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultCard(info: PhoneNumberInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 6.dp,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = info.riskLevel.backgroundColor()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Resultado da análise",
                style = MaterialTheme.typography.overline,
                color = TextSecondary
            )

            Text(
                text = info.number,
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(info.riskLevel.color().copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = info.riskLevel.label(),
                    color = info.riskLevel.color(),
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Divider()

            InfoRow(
                label = "Estado",
                value = info.riskLevel.label()
            )

            info.reportsCount?.let {
                InfoRow(label = "Denúncias", value = it.toString())
            }

            info.category?.let {
                InfoRow(label = "Categoria", value = it)
            }

            info.lastReportedAt?.let {
                InfoRow(
                    label = "Última denúncia",
                    value = formatRelativeDate(it)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 0.dp,
                shape = RoundedCornerShape(12.dp),
                backgroundColor = if (info.shouldBlock) {
                    info.riskLevel.color().copy(alpha = 0.12f)
                } else {
                    MaterialTheme.colors.surface
                }
            ) {
                Text(
                    text = if (info.shouldBlock) {
                        "A chamada seria bloqueada"
                    } else {
                        "A chamada seria permitida"
                    },
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body2,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}

@Composable
private fun HistoryItem(info: PhoneNumberInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(info.riskLevel.color())
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = info.number,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = info.riskLevel.label(),
                    style = MaterialTheme.typography.body2
                )

                info.category?.let {
                    Text(
                        text = "Categoria: $it",
                        style = MaterialTheme.typography.caption
                    )
                }

                info.reportsCount?.let {
                    Text(
                        text = "Denúncias: $it",
                        style = MaterialTheme.typography.caption
                    )
                }
            }

            if (info.shouldBlock) {
                Text(
                    text = "Bloqueado",
                    style = MaterialTheme.typography.caption,
                    color = info.riskLevel.color(),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}