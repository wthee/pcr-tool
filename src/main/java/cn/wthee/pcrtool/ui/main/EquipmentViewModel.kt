package cn.wthee.pcrtool.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import cn.wthee.pcrtool.data.EquipmentRepository
import cn.wthee.pcrtool.database.view.EquipmentMaxData
import cn.wthee.pcrtool.database.view.UniqueEquipmentMaxData
import kotlinx.coroutines.launch


class EquipmentViewModel(
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    var equipments = MutableLiveData<List<EquipmentMaxData>>()
    var uniqueEquip = MutableLiveData<UniqueEquipmentMaxData>()
    var refresh = MutableLiveData<Boolean>()

    //获取装备列表
    fun getEquips(asc: Boolean, name: String) {
        viewModelScope.launch {
            val data = equipmentRepository.getAllEquipments(name)
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

    //获取装备类型
    suspend fun getTypes() = equipmentRepository.getEquipTypes()

    //TODO 分页加载
    val allEquips = Pager(
        PagingConfig(
            pageSize = 30,
            enablePlaceholders = true,
            maxSize = 200
        )
    ) {
        equipmentRepository.getPagingEquipments(name = "")
    }.flow
}
