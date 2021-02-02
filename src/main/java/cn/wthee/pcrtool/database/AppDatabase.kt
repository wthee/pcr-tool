package cn.wthee.pcrtool.database

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.edit
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.dao.CharacterDao
import cn.wthee.pcrtool.data.db.dao.EquipmentDao
import cn.wthee.pcrtool.data.db.dao.EventDao
import cn.wthee.pcrtool.data.db.dao.GachaDao
import cn.wthee.pcrtool.data.db.entity.*
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.utils.Constants.DATABASE_BACKUP_NAME
import cn.wthee.pcrtool.utils.Constants.DATABASE_NAME
import com.umeng.umcrash.UMCrash
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.TimeUnit


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
    version = 65,
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
        val sp = ActivityHelper.instance.currentActivity!!.getSharedPreferences(
            "main",
            Context.MODE_PRIVATE
        )

        /**
         * 自动获取数据库、本地备份数据、远程备份数据
         */
        fun getInstance(): AppDatabase {
            try {
                //尝试打开数据库
                if (File(FileUtil.getDatabasePath(1)).exists()) {
                    buildDatabase(DATABASE_NAME).openHelper.readableDatabase
                }
            } catch (e: Exception) {
                //上传日志
                UMCrash.generateCustomLog("OpenDatabaseException", "更新国服数据结构！！！")
                //启用远程备份数据库
                sp.edit {
                    putBoolean(Constants.SP_BACKUP_CN, true)
                }
                MainScope().launch {
                    ToastUtil.short(ResourcesUtil.getString(R.string.database_remote_backup))
                }
                //打开远程备份
                return instance ?: synchronized(this) {
                    instance
                        ?: buildDatabase(DATABASE_BACKUP_NAME)
                            .also { instance = it }
                }
            }
            //正常打开
            sp.edit {
                putBoolean(Constants.SP_BACKUP_CN, false)
            }
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(DATABASE_NAME)
                        .also { instance = it }
            }
        }


        @SuppressLint("UnsafeOptInUsageError")
        private fun buildDatabase(name: String): AppDatabase {
            return Room.databaseBuilder(
                MyApplication.context,
                AppDatabase::class.java,
                name
            ).fallbackToDestructiveMigration()
                .setAutoCloseTimeout(2, TimeUnit.MINUTES)
                .build()
        }
    }

}
