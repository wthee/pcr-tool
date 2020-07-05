package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.data.CharacterRepository
import cn.wthee.pcrtool.data.EquipmentRepository
import cn.wthee.pcrtool.database.AppDatabase
import cn.wthee.pcrtool.ui.detail.character.CharacterPromotionViewModelFactory
import cn.wthee.pcrtool.ui.detail.character.CharacterSkillViewModelFactory
import cn.wthee.pcrtool.ui.detail.equipment.EquipmentDetailsViewModelFactory
import cn.wthee.pcrtool.ui.main.CharacterViewModelFactory
import cn.wthee.pcrtool.ui.main.EquipmentViewModelFactory


object InjectorUtil {
    private fun getCharacterRepository(): CharacterRepository {
        return CharacterRepository(
            AppDatabase.getInstance().getCharacterDao()
        )
    }

    private fun getEquipmentRepository(): EquipmentRepository {
        return EquipmentRepository(
            AppDatabase.getInstance().getEquipmentDao()
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

    fun provideEquipmentDetailsViewModelFactory(

    ): EquipmentDetailsViewModelFactory {
        val repository = getEquipmentRepository()
        return EquipmentDetailsViewModelFactory(
            repository
        )
    }

    fun provideCharacterSkillViewModelFactory(

    ): CharacterSkillViewModelFactory {
        val repository = getCharacterRepository()
        return CharacterSkillViewModelFactory(
            repository
        )
    }
}