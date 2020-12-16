package cn.wthee.pcrtool.utils

import androidx.preference.PreferenceManager
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.repository.CharacterRepository
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.data.db.repository.GachaRepository
import cn.wthee.pcrtool.database.AppDatabase
import cn.wthee.pcrtool.database.AppDatabaseJP
import cn.wthee.pcrtool.ui.detail.character.attr.CharacterAttrViewModelFactory
import cn.wthee.pcrtool.ui.detail.character.skill.CharacterSkillViewModelFactory
import cn.wthee.pcrtool.ui.detail.equipment.EquipmentDetailsViewModelFactory
import cn.wthee.pcrtool.ui.home.CharacterViewModelFactory
import cn.wthee.pcrtool.ui.home.EquipmentViewModelFactory
import cn.wthee.pcrtool.ui.tool.event.EventViewModelFactory
import cn.wthee.pcrtool.ui.tool.gacha.GachaViewModelFactory

/**
 * viewModel
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

    private fun getEventRepository(): EventRepository {
        return EventRepository.getInstance(
            if (getType() == 1) {
                AppDatabase.getInstance().getEventDao()
            } else {
                AppDatabaseJP.getInstance().getEventDao()
            }
        )
    }

    fun provideCharacterViewModelFactory(): CharacterViewModelFactory {
        val repository = getCharacterRepository()
        return CharacterViewModelFactory(
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

    fun provideEquipmentDetailsViewModelFactory(): EquipmentDetailsViewModelFactory {
        val repository = getEquipmentRepository()
        return EquipmentDetailsViewModelFactory(
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

    fun provideEventViewModelFactory(): EventViewModelFactory {
        val repository = getEventRepository()
        return EventViewModelFactory(repository)
    }
}