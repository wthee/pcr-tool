package cn.wthee.pcrtool.database

import android.annotation.SuppressLint
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.dao.PvpDao
import cn.wthee.pcrtool.data.db.entity.PvpFavoriteData
import cn.wthee.pcrtool.utils.Constants

@Database(
    entities = [
        PvpFavoriteData::class
    ],
    version = 100,
    exportSchema = false
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
            return Room.databaseBuilder(
                MyApplication.context,
                AppPvpDatabase::class.java,
                Constants.DATABASE_PVP
            ).fallbackToDestructiveMigration()
                .build()
        }
    }
}
