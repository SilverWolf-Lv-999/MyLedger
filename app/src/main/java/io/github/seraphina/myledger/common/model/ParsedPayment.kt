package io.github.seraphina.myledger.common.model

data class ParsedPayment(
    val direction: RecordDirection,
    val amountCents: Long,
    val title: String,
    val source: RecordSource
)
