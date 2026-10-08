package io.github.seraphina.myledger.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.seraphina.myledger.common.config.CommonConfig
import io.github.seraphina.myledger.data.entity.LedgerRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LedgerRecordDao {
    @Query("SELECT * FROM ${CommonConfig.RECORD_TABLE} ORDER BY timestamp DESC, id DESC")
    fun observeAll(): Flow<List<LedgerRecordEntity>>

    @Insert
    suspend fun insert(record: LedgerRecordEntity): Long

    @Query("DELETE FROM ${CommonConfig.RECORD_TABLE} WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM ${CommonConfig.RECORD_TABLE}")
    suspend fun deleteAll()

    @Query(
        "SELECT EXISTS(SELECT 1 FROM ${CommonConfig.RECORD_TABLE} " +
            "WHERE signature = :signature AND timestamp >= :since)"
    )
    suspend fun hasRecentSignature(signature: String, since: Long): Boolean
}
