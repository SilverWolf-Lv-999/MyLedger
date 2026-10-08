package io.github.seraphina.myledger.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import io.github.seraphina.myledger.R
import io.github.seraphina.myledger.common.config.ClientConfig

@Composable
fun AmountField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input -> onValueChange(input.filter { it.isDigit() || it == '.' }.take(12)) },
        modifier = modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.field_amount)) },
        prefix = { Text(ClientConfig.CURRENCY_SYMBOL) },
        isError = isError,
        supportingText = if (isError) {
            { Text(stringResource(R.string.amount_invalid)) }
        } else {
            null
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )
}
