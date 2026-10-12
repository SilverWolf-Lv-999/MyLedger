package io.github.seraphina.myledger.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    val isLargeExpense = record.direction == RecordDirection.EXPENSE && record.amountCents > 10_000L
    val shape = RoundedCornerShape(8.dp)
    val itemModifier = if (isLargeExpense) {
        modifier
            .padding(horizontal = 12.dp, vertical = 3.dp)
            .clip(shape)
            .border(width = 1.dp, color = amountColor.copy(alpha = 0.6f), shape = shape)
    } else {
        modifier
    }
    ListItem(
        modifier = itemModifier.clickable(onClick = onClick),
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        leadingContent = { DirectionMark(isIncome, amountColor) },
        headlineContent = {
            Text(record.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingContent = {
            Column {
                Text(
                    text = TimeUtility.formatTime(record.timestamp) + " · " + record.source.label,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (record.note.isNotBlank()) {
                    Text(
                        text = record.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
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
