package cn.wthee.pcrtool.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.NewsDao
import cn.wthee.pcrtool.data.RemoteKeyDao
import cn.wthee.pcrtool.data.entity.NewsTable
import cn.wthee.pcrtool.data.entity.RemoteKey


@Database(
    entities = [
        NewsTable::class,
        RemoteKey::class,
    ],
    version = 18,
    exportSchema = false
)
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


        private fun buildDatabase(): AppNewsDatabase {
            return Room.databaseBuilder(
                MyApplication.context,
                AppNewsDatabase::class.java,
                "news_db"
            ).fallbackToDestructiveMigration().build()
        }
    }

}
