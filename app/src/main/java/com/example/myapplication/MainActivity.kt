package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.datasource.FraudJsonDataSource
import com.example.myapplication.data.manager.FraudNotificationManager
import com.example.myapplication.data.repository.JsonFraudRepository
import com.example.myapplication.search.SearchScreen
import com.example.myapplication.search.SearchViewModel
import com.example.myapplication.search.SearchViewModelFactory
import com.example.myapplication.ui.theme.FraudCallTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FraudNotificationManager.createChannel(this)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
        }

        val dataSource= FraudJsonDataSource(applicationContext)
        val repository= JsonFraudRepository(dataSource)
        val factory= SearchViewModelFactory(repository)

        setContent {
            val viewModel: SearchViewModel = viewModel(factory = factory)

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            FraudCallTheme(darkTheme = uiState.settings.useDarkMode) {
                SearchScreen(viewModel = viewModel)
            }
        }
    }
}