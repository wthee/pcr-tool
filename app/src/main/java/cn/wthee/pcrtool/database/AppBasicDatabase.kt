package cn.wthee.pcrtool.database

import android.annotation.SuppressLint
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.dao.ClanBattleDao
import cn.wthee.pcrtool.data.db.dao.EnemyDao
import cn.wthee.pcrtool.data.db.dao.EquipmentDao
import cn.wthee.pcrtool.data.db.dao.EventDao
import cn.wthee.pcrtool.data.db.dao.ExtraEquipmentDao
import cn.wthee.pcrtool.data.db.dao.GachaDao
import cn.wthee.pcrtool.data.db.dao.QuestDao
import cn.wthee.pcrtool.data.db.dao.SkillDao
import cn.wthee.pcrtool.data.db.dao.UnitDao
import cn.wthee.pcrtool.data.db.entity.FakeEntity
import cn.wthee.pcrtool.data.enums.RegionType
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.DATABASE_NAME_CN
import cn.wthee.pcrtool.utils.Constants.DATABASE_NAME_JP
import cn.wthee.pcrtool.utils.Constants.DATABASE_NAME_TW


@Database(
    entities = [
        FakeEntity::class
    ],
    version = Constants.SQLITE_VERSION,
    exportSchema = false
)
/**
 * 游戏数据
 */
abstract class AppBasicDatabase : RoomDatabase() {

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
        private var instance: AppBasicDatabase? = null

        /**
         * 自动获取数据库、远程备份数据
         */
        fun getInstance(type: RegionType): AppBasicDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(
                    when (type) {
                        RegionType.CN -> DATABASE_NAME_CN
                        RegionType.TW -> DATABASE_NAME_TW
                        RegionType.JP -> DATABASE_NAME_JP
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
        fun buildDatabase(name: String): AppBasicDatabase {
            return Room.databaseBuilder(MyApplication.context, AppBasicDatabase::class.java, name)
                .fallbackToDestructiveMigration()
                .build()
        }
    }

}
