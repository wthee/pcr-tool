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
    fun provideAppBasicDatabase(): AppBasicDatabase {
        return AppBasicDatabase.getInstance(getType())
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
    fun provideAppComicDatabase(): AppComicDatabase {
        return AppComicDatabase.getInstance()
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
        return provideAppBasicDatabase().getGachaDao()
    }

    @Provides
    fun provideUnitDao(): UnitDao {
        return provideAppBasicDatabase().getUnitDao()
    }

    @Provides
    fun provideClanBattleDao(): ClanBattleDao {
        return provideAppBasicDatabase().getClanDao()
    }

    @Provides
    fun provideEquipmentDao(): EquipmentDao {
        return provideAppBasicDatabase().getEquipmentDao()
    }

    @Provides
    fun provideExtraEquipmentDao(): ExtraEquipmentDao {
        return provideAppBasicDatabase().getExtraEquipmentDao()
    }

    @Provides
    fun provideEventDao(): EventDao {
        return provideAppBasicDatabase().getEventDao()
    }

    @Provides
    fun provideSkillDao(): SkillDao {
        return provideAppBasicDatabase().getSkillDao()
    }

    @Provides
    fun provideNewsDao(): NewsDao {
        return provideAppNewsDatabase().getNewsDao()
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
    fun provideComicDao(): ComicDao {
        return provideAppComicDatabase().getComicDao()
    }

    @Provides
    fun provideMockGachaDao(): MockGachaDao {
        return provideMockGachaDatabase().getMockGachaDao()
    }

    @Provides
    fun provideQuestDao(): QuestDao {
        return provideAppBasicDatabase().getQuestDao()
    }

    @Provides
    fun provideEnemyDao(): EnemyDao {
        return provideAppBasicDatabase().getEnemyDao()
    }
}