package cn.wthee.pcrtool.database

import android.annotation.SuppressLint
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.dao.MockGachaDao
import cn.wthee.pcrtool.data.db.entity.MockGachaData
import cn.wthee.pcrtool.data.db.entity.MockGachaResultRecord
import cn.wthee.pcrtool.utils.Constants.DATABASE_MOCK_GACHA


@Database(
    entities = [
        MockGachaData::class,
        MockGachaResultRecord::class
    ],
    version = 1,
    exportSchema = false
)
/**
 * 模拟抽卡数据库
 */
abstract class AppMockGachaDatabase : RoomDatabase() {

    abstract fun getMockGachaDao(): MockGachaDao

    companion object {

        @Volatile
        private var instance: AppMockGachaDatabase? = null

        fun getInstance(): AppMockGachaDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase()
                        .also { instance = it }
            }
        }

        @SuppressLint("UnsafeOptInUsageError")
        private fun buildDatabase(): AppMockGachaDatabase {
            return Room.databaseBuilder(
                MyApplication.context,
                AppMockGachaDatabase::class.java,
                DATABASE_MOCK_GACHA
            ).fallbackToDestructiveMigration()
                .build()
        }
    }

}
