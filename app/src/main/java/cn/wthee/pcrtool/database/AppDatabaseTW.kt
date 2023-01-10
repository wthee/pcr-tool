package cn.wthee.pcrtool.database

import android.annotation.SuppressLint
import android.os.Build
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.dao.*
import cn.wthee.pcrtool.data.db.entity.ExperienceUnit
import cn.wthee.pcrtool.utils.Constants


@Database(
    entities = [
        ExperienceUnit::class
    ],
    version = Constants.SQLITE_VERSION,
    exportSchema = false
)
/**
 * 台服版本数据库
 */
abstract class AppDatabaseTW : RoomDatabase() {

    abstract fun getUnitDao(): UnitDao
    abstract fun getSkillDao(): SkillDao
    abstract fun getEquipmentDao(): EquipmentDao
    abstract fun getExtraEquipmentDao(): ExtraEquipmentDao
    abstract fun getGachaDao(): GachaDao
    abstract fun getEventDao(): EventDao
    abstract fun getClanDao(): ClanBattleDao
    abstract fun getQuestDao(): QuestDao
    abstract fun getEnemyDao(): EnemyDao

    companion object {

        @Volatile
        private var instance: AppDatabaseTW? = null

        /**
         * 自动获取数据库、远程备份数据
         */
        fun getInstance(): AppDatabaseTW {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(
                    if (MyApplication.backupMode) {
                        Constants.DATABASE_BACKUP_NAME_TW
                    } else {
                        Constants.DATABASE_NAME_TW
                    }
                ).also { instance = it }
            }
        }

        /**
         * 关闭数据库
         */
        fun close() {
            instance?.let {
                if (it.isOpen) {
                    it.close()
                }
                instance = null
            }
        }

        @SuppressLint("UnsafeOptInUsageError")
        fun buildDatabase(name: String): AppDatabaseTW {
            return Room.databaseBuilder(MyApplication.context, AppDatabaseTW::class.java, name)
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            db.disableWriteAheadLogging()
                        }
                    }
                })
                .build()
        }
    }

}
