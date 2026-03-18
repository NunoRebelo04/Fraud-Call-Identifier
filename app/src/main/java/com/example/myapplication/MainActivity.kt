package com.example.myapplication

import android.Manifest
import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.manager.FraudNotificationManager
import com.example.myapplication.search.SearchScreen
import com.example.myapplication.search.SearchViewModel
import com.example.myapplication.search.SearchViewModelFactory
import com.example.myapplication.telecom.AppContainer
import com.example.myapplication.ui.theme.FraudCallTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val REQUEST_NOTIFICATIONS = 100
        private const val REQUEST_CALL_SCREENING_ROLE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FraudNotificationManager.createChannel(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_NOTIFICATIONS)
        }

        requestCallScreeningRoleIfNeeded()

        AppContainer.init(applicationContext)
        val repository = AppContainer.provideRepository()
        val factory = SearchViewModelFactory(repository)

        setContent {
            val viewModel: SearchViewModel = viewModel(factory = factory)
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            FraudCallTheme(darkTheme = uiState.settings.useDarkMode) {
                SearchScreen(viewModel = viewModel)
            }
        }
    }

    private fun requestCallScreeningRoleIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return

        val roleManager = getSystemService(RoleManager::class.java)

        if (roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) &&
            !roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
        ) {
            val intent: Intent =
                roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
            startActivityForResult(intent, REQUEST_CALL_SCREENING_ROLE)
        }
    }
}