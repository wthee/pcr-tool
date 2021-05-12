package cn.wthee.pcrtool.database

import android.annotation.SuppressLint
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.dao.NewsDao
import cn.wthee.pcrtool.data.db.dao.RemoteKeyDao
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.db.entity.RemoteKey
import cn.wthee.pcrtool.utils.Constants.DATABASE_NEWS


@Database(
    entities = [
        NewsTable::class,
        RemoteKey::class,
    ],
    version = 62,
    exportSchema = false
)
/**
 * 公告信息数据库
 */
abstract class AppNewsDatabase : RoomDatabase() {

    abstract fun getNewsDao(): NewsDao
    abstract fun getRemoteKeyDao(): RemoteKeyDao

    companion object {

        @Volatile
        private var instance: AppNewsDatabase? = null

        fun getInstance(): AppNewsDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase()
                        .also { instance = it }
            }
        }

        @SuppressLint("UnsafeOptInUsageError")
        private fun buildDatabase(): AppNewsDatabase {
            return Room.databaseBuilder(
                MyApplication.context,
                AppNewsDatabase::class.java,
                DATABASE_NEWS
            ).fallbackToDestructiveMigration()
                .build()
        }
    }

}
