package io.github.seraphina.myledger.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.seraphina.myledger.R
import io.github.seraphina.myledger.service.LedgerKeepAliveService
import io.github.seraphina.myledger.ui.component.InitialAmountDialog
import io.github.seraphina.myledger.ui.state.LedgerUiState
import io.github.seraphina.myledger.utility.AmountUtility

@Composable
fun SettingsScreen(
    uiState: LedgerUiState,
    onSetInitialAmount: (Long) -> Unit,
    onToggleAutoRecord: (Boolean) -> Unit,
    onToggleKeepAlive: (Boolean) -> Unit,
    onClearRecords: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showInitialAmountDialog by rememberSaveable { mutableStateOf(false) }
    var showClearDialog by rememberSaveable { mutableStateOf(false) }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item { SectionHeader(stringResource(R.string.settings_section_balance)) }
        item {
            ListItem(
                modifier = Modifier.clickable { showInitialAmountDialog = true },
                headlineContent = { Text(stringResource(R.string.settings_initial_amount)) },
                supportingContent = {
                    Text(
                        if (uiState.hasInitialAmount) {
                            AmountUtility.formatWithSymbol(uiState.initialAmountCents)
                        } else {
                            stringResource(R.string.settings_initial_amount_unset)
                        }
                    )
                },
                trailingContent = { Icon(Icons.Filled.Edit, contentDescription = null) }
            )
        }
        item { HorizontalDivider(Modifier.padding(horizontal = 16.dp)) }
        item { SectionHeader(stringResource(R.string.settings_section_notification)) }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_notification_access)) },
                supportingContent = {
                    Text(
                        stringResource(
                            if (uiState.notificationAccessGranted) {
                                R.string.settings_notification_granted
                            } else {
                                R.string.settings_notification_denied
                            }
                        )
                    )
                },
                trailingContent = {
                    TextButton(onClick = onOpenNotificationSettings) {
                        Text(stringResource(R.string.settings_open_system))
                    }
                }
            )
        }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_auto_record)) },
                supportingContent = { Text(stringResource(R.string.settings_auto_record_desc)) },
                trailingContent = {
                    Switch(checked = uiState.autoRecordEnabled, onCheckedChange = onToggleAutoRecord)
                }
            )
        }
        item { HorizontalDivider(Modifier.padding(horizontal = 16.dp)) }
        item { SectionHeader(stringResource(R.string.settings_section_background)) }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_keep_alive)) },
                supportingContent = { Text(stringResource(R.string.settings_keep_alive_desc)) },
                trailingContent = {
                    Switch(checked = uiState.keepAliveEnabled, onCheckedChange = onToggleKeepAlive)
                }
            )
        }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_battery_optimization)) },
                supportingContent = {
                    Text(
                        stringResource(
                            if (uiState.batteryOptimizationIgnored) {
                                R.string.settings_battery_ignored
                            } else {
                                R.string.settings_battery_not_ignored
                            }
                        )
                    )
                },
                trailingContent = {
                    TextButton(onClick = { LedgerKeepAliveService.requestIgnoreBatteryOptimizations(context) }) {
                        Text(stringResource(R.string.settings_open_system))
                    }
                }
            )
        }
        item {
            ListItem(
                modifier = Modifier.clickable { LedgerKeepAliveService.openAutoStartSettings(context) },
                headlineContent = { Text(stringResource(R.string.settings_auto_start)) },
                supportingContent = { Text(stringResource(R.string.settings_auto_start_desc)) },
                trailingContent = { Icon(Icons.Filled.Settings, contentDescription = null) }
            )
        }
        item { HorizontalDivider(Modifier.padding(horizontal = 16.dp)) }
        item { SectionHeader(stringResource(R.string.settings_section_data)) }
        item {
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_record_count)) },
                trailingContent = { Text(uiState.records.size.toString()) }
            )
        }
        item {
            ListItem(
                modifier = Modifier.clickable { showClearDialog = true },
                headlineContent = {
                    Text(
                        text = stringResource(R.string.settings_clear_records),
                        color = MaterialTheme.colorScheme.error
                    )
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            )
        }
    }
    if (showInitialAmountDialog) {
        InitialAmountDialog(
            currentAmountCents = uiState.initialAmountCents,
            onDismiss = { showInitialAmountDialog = false },
            onConfirm = { cents ->
                onSetInitialAmount(cents)
                showInitialAmountDialog = false
            }
        )
    }
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(stringResource(R.string.settings_clear_confirm_title)) },
            text = { Text(stringResource(R.string.settings_clear_confirm_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearRecords()
                        showClearDialog = false
                    }
                ) {
                    Text(stringResource(R.string.action_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
    )
}
