package cn.wthee.pcrtool.di

import cn.wthee.pcrtool.data.db.dao.*
import cn.wthee.pcrtool.data.enums.RegionType
import cn.wthee.pcrtool.database.*
import cn.wthee.pcrtool.ui.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    private fun getType() = MainActivity.regionType

    @Singleton
    @Provides
    fun provideAppDatabaseCN(): AppDatabaseCN {
        return AppDatabaseCN.getInstance()
    }

    @Singleton
    @Provides
    fun provideAppDatabaseTW(): AppDatabaseTW {
        return AppDatabaseTW.getInstance()
    }

    @Singleton
    @Provides
    fun provideAppDatabaseJP(): AppDatabaseJP {
        return AppDatabaseJP.getInstance()
    }

    @Singleton
    @Provides
    fun provideAppNewsDatabase(): AppNewsDatabase {
        return AppNewsDatabase.getInstance()
    }

    @Singleton
    @Provides
    fun provideAppTweetDatabase(): AppTweetDatabase {
        return AppTweetDatabase.getInstance()
    }

    @Singleton
    @Provides
    fun provideAppPvpDatabase(): AppPvpDatabase {
        return AppPvpDatabase.getInstance()
    }

    @Singleton
    @Provides
    fun provideMockGachaDatabase(): AppMockGachaDatabase {
        return AppMockGachaDatabase.getInstance()
    }

    @Provides
    fun provideGachaDao(): GachaDao {
        return when (getType()) {
            RegionType.CN -> provideAppDatabaseCN().getGachaDao()
            RegionType.TW -> provideAppDatabaseTW().getGachaDao()
            RegionType.JP -> provideAppDatabaseJP().getGachaDao()
        }
    }

    @Provides
    fun provideUnitDao(): UnitDao {
        return when (getType()) {
            RegionType.CN -> provideAppDatabaseCN().getUnitDao()
            RegionType.TW -> provideAppDatabaseTW().getUnitDao()
            RegionType.JP -> provideAppDatabaseJP().getUnitDao()
        }
    }

    @Provides
    fun provideClanBattleDao(): ClanBattleDao {
        return when (getType()) {
            RegionType.CN -> provideAppDatabaseCN().getClanDao()
            RegionType.TW -> provideAppDatabaseTW().getClanDao()
            RegionType.JP -> provideAppDatabaseJP().getClanDao()
        }
    }

    @Provides
    fun provideEquipmentDao(): EquipmentDao {
        return when (getType()) {
            RegionType.CN -> provideAppDatabaseCN().getEquipmentDao()
            RegionType.TW -> provideAppDatabaseTW().getEquipmentDao()
            RegionType.JP -> provideAppDatabaseJP().getEquipmentDao()
        }
    }

    @Provides
    fun provideExtraEquipmentDao(): ExtraEquipmentDao {
        return when (getType()) {
            RegionType.CN -> provideAppDatabaseCN().getExtraEquipmentDao()
            RegionType.TW -> provideAppDatabaseTW().getExtraEquipmentDao()
            RegionType.JP -> provideAppDatabaseJP().getExtraEquipmentDao()
        }
    }

    @Provides
    fun provideEventDao(): EventDao {
        return when (getType()) {
            RegionType.CN -> provideAppDatabaseCN().getEventDao()
            RegionType.TW -> provideAppDatabaseTW().getEventDao()
            RegionType.JP -> provideAppDatabaseJP().getEventDao()
        }
    }

    @Provides
    fun provideSkillDao(): SkillDao {
        return when (getType()) {
            RegionType.CN -> provideAppDatabaseCN().getSkillDao()
            RegionType.TW -> provideAppDatabaseTW().getSkillDao()
            RegionType.JP -> provideAppDatabaseJP().getSkillDao()
        }
    }

    @Provides
    fun provideNewsDao(): NewsDao {
        return provideAppNewsDatabase().getNewsDao()
    }

    @Provides
    fun provideRemoteKeyDao(): RemoteKeyDao {
        return provideAppNewsDatabase().getRemoteKeyDao()
    }

    @Provides
    fun providePvpDao(): PvpDao {
        return provideAppPvpDatabase().getPvpDao()
    }

    @Provides
    fun provideTweetDao(): TweetDao {
        return provideAppTweetDatabase().getTweetDao()
    }

    @Provides
    fun provideMockGachaDao(): MockGachaDao {
        return provideMockGachaDatabase().getMockGachaDao()
    }

    @Provides
    fun provideQuestDao(): QuestDao {
        return when (getType()) {
            RegionType.CN -> provideAppDatabaseCN().getQuestDao()
            RegionType.TW -> provideAppDatabaseTW().getQuestDao()
            RegionType.JP -> provideAppDatabaseJP().getQuestDao()
        }
    }

    @Provides
    fun provideEnemyDao(): EnemyDao {
        return when (getType()) {
            RegionType.CN -> provideAppDatabaseCN().getEnemyDao()
            RegionType.TW -> provideAppDatabaseTW().getEnemyDao()
            RegionType.JP -> provideAppDatabaseJP().getEnemyDao()
        }
    }
}