package io.github.seraphina.myledger.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.seraphina.myledger.common.config.CommonConfig
import io.github.seraphina.myledger.data.entity.LedgerSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LedgerSettingsDao {
    @Query("SELECT * FROM ${CommonConfig.SETTINGS_TABLE} WHERE id = ${CommonConfig.SETTINGS_ROW_ID}")
    fun observe(): Flow<LedgerSettingsEntity?>

    @Query("SELECT * FROM ${CommonConfig.SETTINGS_TABLE} WHERE id = ${CommonConfig.SETTINGS_ROW_ID}")
    suspend fun find(): LedgerSettingsEntity?

    @Upsert
    suspend fun upsert(settings: LedgerSettingsEntity)
}
