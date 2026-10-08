package io.github.seraphina.myledger.common.model

data class LedgerRecord(
    val id: Long,
    val direction: RecordDirection,
    val amountCents: Long,
    val title: String,
    val source: RecordSource,
    val note: String,
    val timestamp: Long
)
