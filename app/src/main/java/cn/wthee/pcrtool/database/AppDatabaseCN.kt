package cn.wthee.pcrtool.database

import android.annotation.SuppressLint
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.dao.*
import cn.wthee.pcrtool.data.db.entity.*
import cn.wthee.pcrtool.utils.Constants.DATABASE_BACKUP_NAME_CN
import cn.wthee.pcrtool.utils.Constants.DATABASE_NAME_CN


@Database(
    entities = [
        UnitProfile::class,
        Unit6Star::class,
        ActualUnitBackground::class,
        UnitData::class,
        UnitPromotion::class,
        UnitPromotionStatus::class,
        ExperienceUnit::class,
        UnitRarity::class,
        UnitSkillData::class,
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
        UnitComments::class,
        GachaData::class,
        GachaExchange::class,
        CharaStoryStatus::class,
        CharacterType::class,
        EventStoryData::class,
        EventStoryDetail::class,
        EventTopAdv::class,
        HatsuneSchedule::class,
        CampaignSchedule::class,
        RoomUnitComments::class,
        AilmentData::class,
        ClanBattleBossData::class,
        ClanBattleSchedule::class,
        EnemyParameter::class,
        TowerSchedule::class,
        UnitEnemyData::class,
        UnitPromotionBonus::class,
        GuildAdditionalMember::class,
        UnitStatusCoefficient::class,
    ],
    version = BuildConfig.SQLITE_VERSION,
    exportSchema = false
)
/**
 * 国服版本数据库
 */
abstract class AppDatabaseCN : RoomDatabase() {

    abstract fun getUnitDao(): UnitDao
    abstract fun getSkillDao(): SkillDao
    abstract fun getEquipmentDao(): EquipmentDao
    abstract fun getGachaDao(): GachaDao
    abstract fun getEventDao(): EventDao
    abstract fun getClanDao(): ClanBattleDao


    companion object {

        @Volatile
        private var instance: AppDatabaseCN? = null

        /**
         * 自动获取数据库、远程备份数据
         */
        fun getInstance(): AppDatabaseCN {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(
                    if (MyApplication.backupMode) {
                        DATABASE_BACKUP_NAME_CN
                    } else {
                        DATABASE_NAME_CN
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
        fun buildDatabase(name: String): AppDatabaseCN {
            return Room.databaseBuilder(
                MyApplication.context,
                AppDatabaseCN::class.java,
                name
            ).fallbackToDestructiveMigration()
                .build()
        }
    }

}
