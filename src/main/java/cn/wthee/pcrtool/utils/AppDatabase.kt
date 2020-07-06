package cn.wthee.pcrtool.utils

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.CharacterDao
import cn.wthee.pcrtool.data.EnemyDao
import cn.wthee.pcrtool.data.EquipmentDao
import cn.wthee.pcrtool.data.model.*
import cn.wthee.pcrtool.utils.Constants.DATABASE_CN_Name


@Database(
    entities = [
        Character::class,
        Character6Star::class,
        CharacterActualData::class,
        CharacterData::class,
        CharacterLoveRank::class,
        CharacterPromotion::class,
        CharacterPromotionStatus::class,
        CharacterRarity::class,
        CharacterSkillData::class,
        EnemyRewardData::class,
        EquipmentData::class,
        EquipmentCraft::class,
        EquipmentEnhanceRate::class,
        QuestData::class,
        SkillAction::class,
        SkillData::class,
        WaveGroupData::class,
        EnemyData::class
    ],
    version = 1,
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
            return Room.databaseBuilder(
                MyApplication.getContext(),
                AppDatabase::class.java,
                DATABASE_CN_Name
            ).build()
        }
    }

}
