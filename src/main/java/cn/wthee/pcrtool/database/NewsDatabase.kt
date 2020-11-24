package cn.wthee.pcrtool.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.NewsDao
import cn.wthee.pcrtool.data.model.NewsTable


@Database(
    entities = [
        NewsTable::class,
    ],
    version = 18,
    exportSchema = false
)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun getNewsDao(): NewsDao

    companion object {

        @Volatile
        private var instance: NewsDatabase? = null

        fun getInstance(): NewsDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase()
                        .also { instance = it }
            }
        }


        private fun buildDatabase(): NewsDatabase {
            return Room.databaseBuilder(
                MyApplication.context,
                NewsDatabase::class.java,
                "news_db"
            ).fallbackToDestructiveMigration().build()
        }
    }

}
