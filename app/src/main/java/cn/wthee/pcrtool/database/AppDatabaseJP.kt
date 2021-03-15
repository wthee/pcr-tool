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
import cn.wthee.pcrtool.data.entityjp.*
import cn.wthee.pcrtool.utils.Constants


@Database(
    entities = [
        CharacterProfile::class,
        Character6Star::class,
        CharacterActualData::class,
        CharacterDataJP::class,
        CharacterPromotion::class,
        CharacterPromotionStatus::class,
        CharacterExperience::class,
        CharacterRarity::class,
        CharacterSkillDataJP::class,
        EnemyRewardData::class,
        EquipmentCraft::class,
        EquipmentDataJP::class,
        EquipmentEnhanceRate::class,
        EquipmentEnhanceData::class,
        UniqueEquipmentData::class,
        UniqueEquipmentEnhanceData::class,
        UniqueEquipmentEnhanceRate::class,
        QuestDataJP::class,
        SkillAction::class,
        SkillData::class,
        WaveGroupDataJP::class,
        AttackPattern::class,
        GuildData::class,
        CharacterComments::class,
        GachaDataJP::class,
        GachaExchangeJP::class,
        ItemDataJP::class,
        CharacterStoryStatus::class,
        CharacterType::class,
        EventStoryData::class,
        EventStoryDetail::class,
        EventTopAdvJP::class,
        OddsNameData::class,
        HatsuneScheduleJP::class,
        CampaignScheduleJP::class,
        CharacterRoomComments::class,
        AilmentData::class,
        ClanBattleBossDataJP::class,
        EnemyParameterJP::class
    ],
    version = BuildConfig.SQLITE_VERSION,
    exportSchema = false
)
/**
 * 日服版本数据库
 */
abstract class AppDatabaseJP : RoomDatabase() {

    abstract fun getCharacterDao(): CharacterDao
    abstract fun getEquipmentDao(): EquipmentDao
    abstract fun getGachaDao(): GachaDao
    abstract fun getEventDao(): EventDao

    companion object {

        @Volatile
        private var instance: AppDatabaseJP? = null

        /**
         * 自动获取数据库、远程备份数据
         */
        fun getInstance(): AppDatabaseJP {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(
                    if (MyApplication.backupMode) {
                        Constants.DATABASE_BACKUP_NAME_JP
                    } else {
                        Constants.DATABASE_NAME_JP
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
        fun buildDatabase(name: String): AppDatabaseJP {
            return Room.databaseBuilder(
                MyApplication.context,
                AppDatabaseJP::class.java,
                name
            ).fallbackToDestructiveMigration()
                .build()
        }
    }

}
