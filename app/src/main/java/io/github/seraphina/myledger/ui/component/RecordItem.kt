package io.github.seraphina.myledger.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.seraphina.myledger.common.model.LedgerRecord
import io.github.seraphina.myledger.common.model.RecordDirection
import io.github.seraphina.myledger.ui.theme.ledgerAmountColor
import io.github.seraphina.myledger.utility.AmountUtility
import io.github.seraphina.myledger.utility.TimeUtility

@Composable
fun RecordItem(record: LedgerRecord, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val isIncome = record.direction == RecordDirection.INCOME
    val amountColor = ledgerAmountColor(record.direction)
    ListItem(
        modifier = modifier.clickable(onClick = onClick),
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        leadingContent = { DirectionMark(isIncome, amountColor) },
        headlineContent = {
            Text(record.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingContent = {
            Text(recordSupportingText(record), maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        trailingContent = {
            Text(
                text = (if (isIncome) "+" else "-") + AmountUtility.formatWithSymbol(record.amountCents),
                color = amountColor,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
        }
    )
}

private fun recordSupportingText(record: LedgerRecord): String {
    val base = TimeUtility.formatTime(record.timestamp) + " · " + record.source.label
    return if (record.note.isBlank()) base else base + " · " + record.note
}

@Composable
private fun DirectionMark(isIncome: Boolean, color: Color) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isIncome) "+" else "-",
            color = color,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
