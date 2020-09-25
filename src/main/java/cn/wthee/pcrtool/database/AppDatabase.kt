package cn.wthee.pcrtool.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.CharacterDao
import cn.wthee.pcrtool.data.EnemyDao
import cn.wthee.pcrtool.data.EquipmentDao
import cn.wthee.pcrtool.database.entity.*
import cn.wthee.pcrtool.utils.Constants.DATABASE_Name


@Database(
    entities = [
        CharacterProfile::class,
        Character6Star::class,
        CharacterActualData::class,
        CharacterData::class,
        CharacterLoveRank::class,
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
        EnemyData::class,
        CharacterExperience::class,
        AttackPattern::class,
        GuildData::class,
        CharacterExperienceTeam::class,
        CharacterComments::class,
    ],
    version = 12,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getCharacterDao(): CharacterDao
    abstract fun getEquipmentDao(): EquipmentDao
    abstract fun getEnemyDao(): EnemyDao

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
            try {
                return Room.databaseBuilder(
                    MyApplication.getContext(),
                    AppDatabase::class.java,
                    DATABASE_Name
                ).fallbackToDestructiveMigration().build()
            }catch (e : Exception){
                //TODO mirgin
                return Room.databaseBuilder(
                    MyApplication.getContext(),
                    AppDatabase::class.java,
                    DATABASE_Name
                ).fallbackToDestructiveMigration().build()
            }

        }
    }

}
