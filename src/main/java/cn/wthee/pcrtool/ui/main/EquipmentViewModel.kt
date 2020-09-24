package cn.wthee.pcrtool.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.EquipmentRepository
import cn.wthee.pcrtool.database.entity.EquipmentMaxData
import cn.wthee.pcrtool.database.entity.UniqueEquipmentMaxData
import kotlinx.coroutines.launch


class EquipmentViewModel(
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    var equipments = MutableLiveData<List<EquipmentMaxData>>()
    var uniqueEquip = MutableLiveData<UniqueEquipmentMaxData>()
    var isLoading = MutableLiveData<Boolean>()
    var refresh = MutableLiveData<Boolean>()
    var isList = MutableLiveData<Boolean>()

    //获取装备列表
    fun getEquips(asc: Boolean, name: String) {
        isLoading.postValue(true)
        viewModelScope.launch {
            val data = equipmentRepository.getAllEquipments(name)
            isLoading.postValue(false)
            refresh.postValue(false)
            if (asc) {
                data.sortedBy { it.promotionLevel }
            } else {
                data.sortedByDescending { it.promotionLevel }
            }
            equipments.postValue(data)
        }
    }

    //专武信息
    fun getUniqueEquipInfos(uid: Int) {
        viewModelScope.launch {
            val data = equipmentRepository.getUniqueEquipInfos(uid)
            uniqueEquip.postValue(data)
        }
    }
}
