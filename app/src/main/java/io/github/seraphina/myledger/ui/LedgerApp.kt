package io.github.seraphina.myledger.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import io.github.seraphina.myledger.R
import io.github.seraphina.myledger.ui.component.InitialAmountDialog
import io.github.seraphina.myledger.ui.component.ManualRecordDialog
import io.github.seraphina.myledger.ui.screen.LedgerScreen
import io.github.seraphina.myledger.ui.screen.SettingsScreen
import io.github.seraphina.myledger.ui.viewmodel.LedgerViewModel
import io.github.seraphina.myledger.utility.NotificationAccessUtility

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedgerApp(viewModel: LedgerViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var currentTab by rememberSaveable { mutableIntStateOf(0) }
    var showInitialAmountDialog by rememberSaveable { mutableStateOf(false) }
    var showManualRecordDialog by rememberSaveable { mutableStateOf(false) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(uiState.isLoading, uiState.hasInitialAmount) {
        if (!uiState.isLoading && !uiState.hasInitialAmount) {
            showInitialAmountDialog = true
        }
    }

    LaunchedEffect(uiState.keepAliveEnabled, uiState.notificationAccessGranted) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || !uiState.keepAliveEnabled) return@LaunchedEffect
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(if (currentTab == 0) R.string.nav_ledger else R.string.nav_settings))
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_ledger)) }
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_settings)) }
                )
            }
        },
        floatingActionButton = {
            if (currentTab == 0) {
                ExtendedFloatingActionButton(
                    onClick = { showManualRecordDialog = true },
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text(stringResource(R.string.action_add_record)) }
                )
            }
        }
    ) { innerPadding ->
        if (currentTab == 0) {
            LedgerScreen(
                uiState = uiState,
                onOpenNotificationSettings = { NotificationAccessUtility.openSettings(context) },
                onDeleteRecord = viewModel::deleteRecord,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            SettingsScreen(
                uiState = uiState,
                onSetInitialAmount = viewModel::setInitialAmount,
                onToggleAutoRecord = viewModel::setAutoRecordEnabled,
                onToggleKeepAlive = viewModel::setKeepAliveEnabled,
                onClearRecords = viewModel::clearRecords,
                onOpenNotificationSettings = { NotificationAccessUtility.openSettings(context) },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    if (showInitialAmountDialog) {
        InitialAmountDialog(
            currentAmountCents = uiState.initialAmountCents,
            onDismiss = { showInitialAmountDialog = false },
            onConfirm = { cents ->
                viewModel.setInitialAmount(cents)
                showInitialAmountDialog = false
            }
        )
    }
    if (showManualRecordDialog) {
        ManualRecordDialog(
            onDismiss = { showManualRecordDialog = false },
            onConfirm = { direction, amountCents, note ->
                viewModel.addManualRecord(direction, amountCents, note)
                showManualRecordDialog = false
            }
        )
    }
}
