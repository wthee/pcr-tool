package cn.wthee.pcrtool.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.dao.CharacterDao
import cn.wthee.pcrtool.data.db.dao.EquipmentDao
import cn.wthee.pcrtool.data.db.dao.EventDao
import cn.wthee.pcrtool.data.db.dao.GachaDao
import cn.wthee.pcrtool.data.db.entity.*
import cn.wthee.pcrtool.data.db.entityjp.*
import cn.wthee.pcrtool.utils.Constants.DATABASE_Name_JP


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
    ],
    version = 63,
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

        fun getInstance(): AppDatabaseJP {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase()
                        .also { instance = it }
            }
        }


        private fun buildDatabase(): AppDatabaseJP {
            return Room.databaseBuilder(
                MyApplication.context,
                AppDatabaseJP::class.java,
                DATABASE_Name_JP
            ).fallbackToDestructiveMigration().build()
        }
    }

}
