package cn.wthee.pcrtool.database

import android.annotation.SuppressLint
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.dao.ComicDao
import cn.wthee.pcrtool.data.db.dao.RemoteKeyDao
import cn.wthee.pcrtool.data.db.entity.ComicData
import cn.wthee.pcrtool.data.db.entity.RemoteKey
import cn.wthee.pcrtool.utils.Constants.DATABASE_COMIC


@Database(
    entities = [
        ComicData::class,
        RemoteKey::class,
    ],
    version = 1,
    exportSchema = false
)
/**
 * 漫画信息数据库
 */
abstract class AppComicDatabase : RoomDatabase() {

    abstract fun getComicDao(): ComicDao
    abstract fun getRemoteKeyDao(): RemoteKeyDao

    companion object {

        @Volatile
        private var instance: AppComicDatabase? = null

        fun getInstance(): AppComicDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase()
                        .also { instance = it }
            }
        }

        @SuppressLint("UnsafeOptInUsageError")
        private fun buildDatabase(): AppComicDatabase {
            return Room.databaseBuilder(
                MyApplication.context,
                AppComicDatabase::class.java,
                DATABASE_COMIC
            ).fallbackToDestructiveMigration()
                .build()
        }
    }

}
