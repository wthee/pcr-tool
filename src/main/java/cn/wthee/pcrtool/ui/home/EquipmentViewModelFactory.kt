package cn.wthee.pcrtool.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository

class EquipmentViewModelFactory(
    private val equipmentRepository: EquipmentRepository
) : ViewModelProvider.NewInstanceFactory() {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EquipmentViewModel(equipmentRepository) as T
    }
}