package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.wthee.pcrtool.data.db.repository.CharacterRepository
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository

class CharacterAttrViewModelFactory(
    private val repository: CharacterRepository,
    private val equipmentRepository: EquipmentRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CharacterAttrViewModel(
            repository,
            equipmentRepository
        ) as T
    }
}