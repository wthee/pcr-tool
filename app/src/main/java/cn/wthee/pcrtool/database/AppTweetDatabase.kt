package cn.wthee.pcrtool.database

import android.annotation.SuppressLint
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.dao.RemoteKeyDao
import cn.wthee.pcrtool.data.db.dao.TweetDao
import cn.wthee.pcrtool.data.db.entity.RemoteKey
import cn.wthee.pcrtool.data.db.entity.TweetData
import cn.wthee.pcrtool.utils.Constants.DATABASE_TWEET


@Database(
    entities = [
        TweetData::class,
        RemoteKey::class,
    ],
    version = 130,
    exportSchema = false
)
/**
 * 推特信息数据库
 */
abstract class AppTweetDatabase : RoomDatabase() {

    abstract fun getTweetDao(): TweetDao
    abstract fun getRemoteKeyDao(): RemoteKeyDao

    companion object {

        @Volatile
        private var instance: AppTweetDatabase? = null

        fun getInstance(): AppTweetDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase()
                        .also { instance = it }
            }
        }

        @SuppressLint("UnsafeOptInUsageError")
        private fun buildDatabase(): AppTweetDatabase {
            return Room.databaseBuilder(
                MyApplication.context,
                AppTweetDatabase::class.java,
                DATABASE_TWEET
            ).fallbackToDestructiveMigration()
                .build()
        }
    }

}
