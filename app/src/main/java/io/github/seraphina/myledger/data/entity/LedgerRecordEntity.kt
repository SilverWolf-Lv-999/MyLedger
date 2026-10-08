package io.github.seraphina.myledger.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import io.github.seraphina.myledger.common.config.CommonConfig
import io.github.seraphina.myledger.common.model.RecordDirection
import io.github.seraphina.myledger.common.model.RecordSource

@Entity(
    tableName = CommonConfig.RECORD_TABLE,
    indices = [Index("timestamp"), Index("signature")]
)
data class LedgerRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val direction: RecordDirection,
    val amountCents: Long,
    val title: String,
    val source: RecordSource,
    val note: String,
    val signature: String,
    val timestamp: Long
)
