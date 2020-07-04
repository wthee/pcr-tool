package cn.wthee.pcrtool.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.EquipmentRepository
import cn.wthee.pcrtool.data.model.EquipmentData
import kotlinx.coroutines.launch


class EquipmentViewModel internal constructor(
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    var equipments = MutableLiveData<List<EquipmentData>>()
    var loading = MutableLiveData<Boolean>()
    var refresh = MutableLiveData<Boolean>()
    var isList = MutableLiveData<Boolean>()

    //获取装备列表
    fun getEquips() {
        viewModelScope.launch {
            val data = equipmentRepository.getAllEquipments()
            if (data.isEmpty()) {
                loading.postValue(true)
            } else {
                loading.postValue(false)
                refresh.postValue(false)
            }
            equipments.postValue(data)
        }
    }
}
