package cn.wthee.pcrtool.database

import android.annotation.SuppressLint
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.dao.CharacterDao
import cn.wthee.pcrtool.data.db.dao.EquipmentDao
import cn.wthee.pcrtool.data.db.dao.EventDao
import cn.wthee.pcrtool.data.db.dao.GachaDao
import cn.wthee.pcrtool.data.entity.*
import cn.wthee.pcrtool.utils.Constants.DATABASE_BACKUP_NAME
import cn.wthee.pcrtool.utils.Constants.DATABASE_NAME


@Database(
    entities = [
        CharacterProfile::class,
        Character6Star::class,
        CharacterActualData::class,
        CharacterData::class,
        CharacterPromotion::class,
        CharacterPromotionStatus::class,
        CharacterExperience::class,
        CharacterRarity::class,
        CharacterSkillData::class,
        EnemyRewardData::class,
        EquipmentCraft::class,
        EquipmentData::class,
        EquipmentEnhanceRate::class,
        EquipmentEnhanceData::class,
        UniqueEquipmentData::class,
        UniqueEquipmentEnhanceData::class,
        UniqueEquipmentEnhanceRate::class,
        QuestData::class,
        SkillAction::class,
        SkillData::class,
        WaveGroupData::class,
        AttackPattern::class,
        GuildData::class,
        CharacterComments::class,
        GachaData::class,
        GachaExchange::class,
        ItemData::class,
        CharacterStoryStatus::class,
        CharacterType::class,
        EventStoryData::class,
        EventStoryDetail::class,
        EventTopAdv::class,
        OddsNameData::class,
        HatsuneSchedule::class,
        CampaignSchedule::class,
        CharacterRoomComments::class,
        AilmentData::class,
    ],
    version = BuildConfig.SQLITE_VERSION,
    exportSchema = false
)
/**
 * 国服版本数据库
 */
abstract class AppDatabase : RoomDatabase() {

    abstract fun getCharacterDao(): CharacterDao
    abstract fun getEquipmentDao(): EquipmentDao
    abstract fun getGachaDao(): GachaDao
    abstract fun getEventDao(): EventDao


    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        /**
         * 自动获取数据库、远程备份数据
         */
        fun getInstance(): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(
                    if (MyApplication.backupMode) {
                        DATABASE_BACKUP_NAME
                    } else {
                        DATABASE_NAME
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
        fun buildDatabase(name: String): AppDatabase {
            return Room.databaseBuilder(
                MyApplication.context,
                AppDatabase::class.java,
                name
            ).fallbackToDestructiveMigration()
                .build()
        }
    }

}
