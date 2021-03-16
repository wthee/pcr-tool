package cn.wthee.pcrtool.utils

import androidx.preference.PreferenceManager
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.repository.*
import cn.wthee.pcrtool.database.AppDatabase
import cn.wthee.pcrtool.database.AppDatabaseJP
import cn.wthee.pcrtool.database.AppPvpDatabase
import cn.wthee.pcrtool.viewmodel.*

/**
 * ViewModel 注射工具
 */
object InjectorUtil {
    private fun getType() =
        PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
            .getString("change_database", "1")?.toInt() ?: 1

    private fun getCharacterRepository(): CharacterRepository {
        return CharacterRepository.getInstance(
            if (getType() == 1) {
                AppDatabase.getInstance().getCharacterDao()
            } else {
                AppDatabaseJP.getInstance().getCharacterDao()
            }
        )
    }

    private fun getEquipmentRepository(): EquipmentRepository {
        return EquipmentRepository.getInstance(
            if (getType() == 1) {
                AppDatabase.getInstance().getEquipmentDao()
            } else {
                AppDatabaseJP.getInstance().getEquipmentDao()
            }
        )
    }

    private fun getGachaRepository(): GachaRepository {
        return GachaRepository.getInstance(
            if (getType() == 1) {
                AppDatabase.getInstance().getGachaDao()
            } else {
                AppDatabaseJP.getInstance().getGachaDao()
            }
        )
    }

    private fun getPvpRepository(): PvpRepository {
        return PvpRepository.getInstance(
            AppPvpDatabase.getInstance().getPvpDao()
        )
    }

    private fun getEventRepository(): EventRepository {
        return EventRepository.getInstance(
            if (getType() == 1) {
                AppDatabase.getInstance().getEventDao()
            } else {
                AppDatabaseJP.getInstance().getEventDao()
            }
        )
    }

    private fun getClanRepository(): ClanRepository {
        return ClanRepository.getInstance(
            if (getType() == 1) {
                AppDatabase.getInstance().getClanDao()
            } else {
                AppDatabaseJP.getInstance().getClanDao()
            }
        )
    }

    fun provideCharacterViewModelFactory(): CharacterViewModelFactory {
        val repository = getCharacterRepository()
        return CharacterViewModelFactory(
            repository
        )
    }

    fun provideGuildViewModelFactory(): GuildViewModelFactory {
        val repository = getCharacterRepository()
        return GuildViewModelFactory(
            repository
        )
    }

    fun provideCharacterAttrViewModelFactory(): CharacterAttrViewModelFactory {
        val repository1 = getCharacterRepository()
        val repository2 = getEquipmentRepository()
        return CharacterAttrViewModelFactory(
            repository1, repository2
        )
    }

    fun provideEquipmentViewModelFactory(): EquipmentViewModelFactory {
        val repository = getEquipmentRepository()
        return EquipmentViewModelFactory(
            repository
        )
    }

    fun provideCharacterSkillViewModelFactory(): CharacterSkillViewModelFactory {
        val repository = getCharacterRepository()
        return CharacterSkillViewModelFactory(
            repository
        )
    }

    fun provideGachaViewModelFactory(): GachaViewModelFactory {
        val repository = getGachaRepository()
        return GachaViewModelFactory(repository)
    }

    fun providePvpViewModelFactory(): PvpLikedViewModelFactory {
        val repository = getPvpRepository()
        return PvpLikedViewModelFactory(repository)
    }

    fun provideEventViewModelFactory(): EventViewModelFactory {
        val repository = getEventRepository()
        return EventViewModelFactory(repository)
    }

    fun provideCalendarViewModelFactory(): CalendarViewModelFactory {
        val repository = getEventRepository()
        return CalendarViewModelFactory(repository)
    }

    fun provideClanViewModelFactory(): ClanViewModelFactory {
        val repository = getClanRepository()
        return ClanViewModelFactory(repository)
    }
}