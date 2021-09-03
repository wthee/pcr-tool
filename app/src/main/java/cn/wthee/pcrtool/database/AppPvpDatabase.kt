package cn.wthee.pcrtool.database

import android.annotation.SuppressLint
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.dao.PvpDao
import cn.wthee.pcrtool.data.db.entity.PvpFavoriteData
import cn.wthee.pcrtool.data.db.entity.PvpHistoryData
import cn.wthee.pcrtool.utils.Constants

@Database(
    entities = [
        PvpFavoriteData::class,
        PvpHistoryData::class,
    ],
    version = 101,
    exportSchema = false,
)
/**
 * 竞技场收藏信息数据库
 */
abstract class AppPvpDatabase : RoomDatabase() {

    abstract fun getPvpDao(): PvpDao

    companion object {

        @Volatile
        private var instance: AppPvpDatabase? = null

        fun getInstance(): AppPvpDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase()
                        .also { instance = it }
            }
        }


        @SuppressLint("UnsafeOptInUsageError")
        private fun buildDatabase(): AppPvpDatabase {
            val MIGRATION_100_101 = object : Migration(100, 101) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL(
                        "CREATE TABLE `pvp_history` (`date` TEXT, `defs` TEXT, " +
                                "PRIMARY KEY(`defs`))"
                    )
                }
            }

            return Room.databaseBuilder(
                MyApplication.context,
                AppPvpDatabase::class.java,
                Constants.DATABASE_PVP
            ).addMigrations(MIGRATION_100_101)
                .build()
        }
    }


}
