package io.github.seraphina.myledger.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.seraphina.myledger.common.config.CommonConfig

@Entity(tableName = CommonConfig.SETTINGS_TABLE)
data class LedgerSettingsEntity(
    @PrimaryKey val id: Long = CommonConfig.SETTINGS_ROW_ID,
    val initialAmountCents: Long?,
    val autoRecordEnabled: Boolean,
    @ColumnInfo(defaultValue = "1") val keepAliveEnabled: Boolean
)
