package cn.wthee.pcrtool.di

import cn.wthee.pcrtool.data.db.dao.*
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
            2 -> provideAppDatabaseCN().getGachaDao()
            3 -> provideAppDatabaseTW().getGachaDao()
            else -> provideAppDatabaseJP().getGachaDao()
        }
    }

    @Provides
    fun provideUnitDao(): UnitDao {
        return when (getType()) {
            2 -> provideAppDatabaseCN().getUnitDao()
            3 -> provideAppDatabaseTW().getUnitDao()
            else -> provideAppDatabaseJP().getUnitDao()
        }
    }

    @Provides
    fun provideClanBattleDao(): ClanBattleDao {
        return when (getType()) {
            2 -> provideAppDatabaseCN().getClanDao()
            3 -> provideAppDatabaseTW().getClanDao()
            else -> provideAppDatabaseJP().getClanDao()
        }
    }

    @Provides
    fun provideEquipmentDao(): EquipmentDao {
        return when (getType()) {
            2 -> provideAppDatabaseCN().getEquipmentDao()
            3 -> provideAppDatabaseTW().getEquipmentDao()
            else -> provideAppDatabaseJP().getEquipmentDao()
        }
    }

    @Provides
    fun provideEventDao(): EventDao {
        return when (getType()) {
            2 -> provideAppDatabaseCN().getEventDao()
            3 -> provideAppDatabaseTW().getEventDao()
            else -> provideAppDatabaseJP().getEventDao()
        }
    }

    @Provides
    fun provideSkillDao(): SkillDao {
        return when (getType()) {
            2 -> provideAppDatabaseCN().getSkillDao()
            3 -> provideAppDatabaseTW().getSkillDao()
            else -> provideAppDatabaseJP().getSkillDao()
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
}