package cn.wthee.pcrtool.di

import cn.wthee.pcrtool.data.db.dao.*
import cn.wthee.pcrtool.database.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    private fun getType() = getDatabaseType()

    @Singleton
    @Provides
    fun provideAppDatabase(): AppDatabase {
        return AppDatabase.getInstance()
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

    @Provides
    fun provideGachaDao(): GachaDao {
        return if (getType() == 1) {
            provideAppDatabase().getGachaDao()
        } else {
            provideAppDatabaseJP().getGachaDao()
        }
    }

    @Provides
    fun provideUnitDao(): UnitDao {
        return if (getType() == 1) {
            provideAppDatabase().getUnitDao()
        } else {
            provideAppDatabaseJP().getUnitDao()
        }
    }

    @Provides
    fun provideClanBattleDao(): ClanBattleDao {
        return if (getType() == 1) {
            provideAppDatabase().getClanDao()
        } else {
            provideAppDatabaseJP().getClanDao()
        }
    }

    @Provides
    fun provideEquipmentDao(): EquipmentDao {
        return if (getType() == 1) {
            provideAppDatabase().getEquipmentDao()
        } else {
            provideAppDatabaseJP().getEquipmentDao()
        }
    }

    @Provides
    fun provideEventDao(): EventDao {
        return if (getType() == 1) {
            provideAppDatabase().getEventDao()
        } else {
            provideAppDatabaseJP().getEventDao()
        }
    }

    @Provides
    fun provideSkillDao(): SkillDao {
        return if (getType() == 1) {
            provideAppDatabase().getSkillDao()
        } else {
            provideAppDatabaseJP().getSkillDao()
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
}