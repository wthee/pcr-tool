package cn.wthee.pcrtool.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.CharacterDao
import cn.wthee.pcrtool.data.EquipmentDao
import cn.wthee.pcrtool.data.EventDao
import cn.wthee.pcrtool.data.GachaDao
import cn.wthee.pcrtool.data.entity.*
import cn.wthee.pcrtool.utils.Constants.DATABASE_Name


@Database(
    entities = [
        CharacterProfile::class,
        Character6Star::class,
        CharacterActualData::class,
        CharacterData::class,
        CharacterPromotion::class,
        CharacterPromotionStatus::class,
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
        CharacterExperience::class,
        AttackPattern::class,
        GuildData::class,
        CharacterExperienceTeam::class,
        CharacterComments::class,
        GachaData::class,
        GachaExchange::class,
        ItemData::class,
        CharacterStoryStatus::class,
        CharacterType::class,
        EventStoryData::class,
        EventStoryDetail::class,
    ],
    version = 20,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getCharacterDao(): CharacterDao
    abstract fun getEquipmentDao(): EquipmentDao
    abstract fun getGachaDao(): GachaDao
    abstract fun getEventDao(): EventDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(): AppDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase()
                        .also { instance = it }
            }
        }


        private fun buildDatabase(): AppDatabase {
            return Room.databaseBuilder(
                MyApplication.context,
                AppDatabase::class.java,
                DATABASE_Name
            ).fallbackToDestructiveMigration().build()
        }
    }

}
