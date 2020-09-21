package cn.wthee.pcrtool.utils

import androidx.preference.PreferenceManager
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.CharacterRepository
import cn.wthee.pcrtool.data.EnemyRepository
import cn.wthee.pcrtool.data.EquipmentRepository
import cn.wthee.pcrtool.database.AppDatabase
import cn.wthee.pcrtool.database.AppDatabaseJP
import cn.wthee.pcrtool.ui.detail.character.CharacterPromotionViewModelFactory
import cn.wthee.pcrtool.ui.detail.character.CharacterSkillViewModelFactory
import cn.wthee.pcrtool.ui.detail.equipment.EquipmentDetailsViewModelFactory
import cn.wthee.pcrtool.ui.main.CharacterViewModelFactory
import cn.wthee.pcrtool.ui.main.EnemyViewModelFactory
import cn.wthee.pcrtool.ui.main.EquipmentViewModelFactory


object InjectorUtil {
    private fun getType() =
        PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext())
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

    private fun getEnemyRepository(): EnemyRepository {
        return EnemyRepository.getInstance(
            if (getType() == 1) {
                AppDatabase.getInstance().getEnemyDao()
            } else {
                AppDatabaseJP.getInstance().getEnemyDao()
            }
        )
    }

    fun provideCharacterViewModelFactory(): CharacterViewModelFactory {
        val repository = getCharacterRepository()
        return CharacterViewModelFactory(
            repository
        )
    }

    fun providePromotionViewModelFactory(): CharacterPromotionViewModelFactory {
        val repository1 = getCharacterRepository()
        val repository2 = getEquipmentRepository()
        return CharacterPromotionViewModelFactory(
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

    fun provideEnemyViewModelFactory(): EnemyViewModelFactory {
        val repository = getEnemyRepository()
        return EnemyViewModelFactory(
            repository
        )
    }
}