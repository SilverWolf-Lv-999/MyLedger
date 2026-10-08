package io.github.seraphina.myledger.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import io.github.seraphina.myledger.R
import io.github.seraphina.myledger.utility.AmountUtility

@Composable
fun InitialAmountDialog(
    currentAmountCents: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    var amountText by rememberSaveable {
        mutableStateOf(if (currentAmountCents > 0L) AmountUtility.format(currentAmountCents).replace(",", "") else "")
    }
    val amountCents = AmountUtility.parseInputCents(amountText)
    val isError = amountText.isNotBlank() && (amountCents == null || amountCents < 0L)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_initial_amount_title)) },
        text = {
            AmountField(
                value = amountText,
                onValueChange = { amountText = it },
                isError = isError
            )
        },
        confirmButton = {
            TextButton(
                onClick = { amountCents?.let(onConfirm) },
                enabled = amountCents != null && amountCents >= 0L
            ) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
