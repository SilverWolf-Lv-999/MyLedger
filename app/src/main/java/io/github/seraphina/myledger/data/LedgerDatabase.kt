package io.github.seraphina.myledger.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.seraphina.myledger.common.config.CommonConfig
import io.github.seraphina.myledger.data.dao.LedgerRecordDao
import io.github.seraphina.myledger.data.dao.LedgerSettingsDao
import io.github.seraphina.myledger.data.entity.LedgerRecordEntity
import io.github.seraphina.myledger.data.entity.LedgerSettingsEntity

@Database(
    entities = [LedgerRecordEntity::class, LedgerSettingsEntity::class],
    version = 2,
    exportSchema = false
)
abstract class LedgerDatabase : RoomDatabase() {
    abstract fun recordDao(): LedgerRecordDao

    abstract fun settingsDao(): LedgerSettingsDao

    companion object {
        @Volatile
        private var instance: LedgerDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE ${CommonConfig.SETTINGS_TABLE} " +
                        "ADD COLUMN keepAliveEnabled INTEGER NOT NULL DEFAULT 1"
                )
            }
        }

        fun get(context: Context): LedgerDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                LedgerDatabase::class.java,
                CommonConfig.DATABASE_NAME
            ).addMigrations(MIGRATION_1_2).build().also { instance = it }
        }
    }
}
