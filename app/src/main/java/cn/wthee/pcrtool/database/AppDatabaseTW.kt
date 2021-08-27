package cn.wthee.pcrtool.database

import android.annotation.SuppressLint
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.dao.*
import cn.wthee.pcrtool.data.db.entity.*
import cn.wthee.pcrtool.data.db.entityTW.EnemyParameterTW
import cn.wthee.pcrtool.data.db.entityTW.UnitDataTW
import cn.wthee.pcrtool.data.db.entityjp.*
import cn.wthee.pcrtool.utils.Constants


@Database(
    entities = [
        UnitProfile::class,
        Unit6Star::class,
        ActualUnitBackground::class,
        UnitDataTW::class,
        UnitPromotion::class,
        UnitPromotionStatus::class,
        ExperienceUnit::class,
        UnitRarity::class,
        UnitSkillDataJP::class,
        EnemyRewardData::class,
        EquipmentCraft::class,
        EquipmentData::class,
        EquipmentEnhanceRate::class,
        EquipmentEnhanceData::class,
        UniqueEquipmentData::class,
        UniqueEquipmentEnhanceData::class,
        UniqueEquipmentEnhanceRate::class,
        QuestDataJP::class,
        SkillAction::class,
        SkillDataJP::class,
        WaveGroupDataJP::class,
        AttackPattern::class,
        GuildData::class,
        UnitComments::class,
        GachaDataJP::class,
        GachaExchangeJP::class,
        CharaStoryStatus::class,
        CharacterType::class,
        EventStoryData::class,
        EventStoryDetail::class,
        EventTopAdvJP::class,
        HatsuneScheduleJP::class,
        CampaignScheduleJP::class,
        RoomUnitComments::class,
        AilmentData::class,
        ClanBattleBossDataJP::class,
        ClanBattleSchedule::class,
        EnemyParameterTW::class,
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
 * 台服版本数据库
 */
abstract class AppDatabaseTW : RoomDatabase() {

    abstract fun getUnitDao(): UnitDao
    abstract fun getSkillDao(): SkillDao
    abstract fun getEquipmentDao(): EquipmentDao
    abstract fun getGachaDao(): GachaDao
    abstract fun getEventDao(): EventDao
    abstract fun getClanDao(): ClanBattleDao

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
            return Room.databaseBuilder(
                MyApplication.context,
                AppDatabaseTW::class.java,
                name
            ).fallbackToDestructiveMigration()
                .build()
        }
    }

}