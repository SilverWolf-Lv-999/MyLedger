package io.github.seraphina.myledger.common.model

data class LedgerSettings(
    val initialAmountCents: Long?,
    val autoRecordEnabled: Boolean,
    val keepAliveEnabled: Boolean
)
