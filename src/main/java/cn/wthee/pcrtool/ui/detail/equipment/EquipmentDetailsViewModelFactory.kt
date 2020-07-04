package cn.wthee.pcrtool.ui.detail.equipment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.wthee.pcrtool.data.EquipmentRepository

class EquipmentDetailsViewModelFactory(
    private val equipmentRepository: EquipmentRepository
) : ViewModelProvider.NewInstanceFactory() {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EquipmentDetailsViewModel(equipmentRepository) as T
    }
}