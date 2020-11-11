package cn.wthee.pcrtool.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import cn.wthee.pcrtool.data.EquipmentRepository
import cn.wthee.pcrtool.data.view.EquipmentMaxData
import cn.wthee.pcrtool.data.view.UniqueEquipmentMaxData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class EquipmentViewModel(
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    lateinit var equipments: Flow<PagingData<EquipmentMaxData>>

    var updateEquip = MutableLiveData<Boolean>()
    var equipmentCounts = MutableLiveData<Int>()
    var uniqueEquip = MutableLiveData<UniqueEquipmentMaxData>()

    //获取装备列表
    fun getEquips(name: String) {
        viewModelScope.launch {
            equipments = Pager(
                PagingConfig(
                    pageSize = 50,
                    enablePlaceholders = false,
                    maxSize = 500
                )
            ) {
                equipmentRepository.getPagingEquipments(
                    name,
                    EquipmentListFragment.equipFilterParams
                )
            }.flow
            equipmentCounts.postValue(
                equipmentRepository.getEquipmentCount(
                    name,
                    EquipmentListFragment.equipFilterParams
                )
            )
            updateEquip.postValue(true)
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

}
