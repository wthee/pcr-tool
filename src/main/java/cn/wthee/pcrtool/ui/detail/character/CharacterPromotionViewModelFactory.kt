package cn.wthee.pcrtool.ui.detail.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.wthee.pcrtool.data.CharacterRepository
import cn.wthee.pcrtool.data.EquipmentRepository

class CharacterPromotionViewModelFactory(
    private val repository: CharacterRepository,
    private val equipmentRepository: EquipmentRepository
) : ViewModelProvider.NewInstanceFactory() {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CharacterPromotionViewModel(
            repository,
            equipmentRepository
        ) as T
    }
}