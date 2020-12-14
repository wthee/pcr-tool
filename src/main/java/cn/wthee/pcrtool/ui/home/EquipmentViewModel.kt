package cn.wthee.pcrtool.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.db.view.UniqueEquipmentMaxData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class EquipmentViewModel(
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    lateinit var equipments: Flow<PagingData<EquipmentMaxData>>
    var updateEquip = MutableLiveData<Boolean>()
    var reset = MutableLiveData<Boolean>()
    var equipmentCounts = MutableLiveData<Int>()
    var uniqueEquip = MutableLiveData<UniqueEquipmentMaxData>()

    //获取装备列表
    fun getEquips(name: String) {
        viewModelScope.launch {
            equipments = Pager(
                PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false
                )
            ) {
                equipmentRepository.getPagingEquipments(
                    name,
                    EquipmentListFragment.equipFilterParams
                )
            }.flow.cachedIn(viewModelScope)
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
    fun getUniqueEquipInfos(uid: Int, lv: Int) {
        viewModelScope.launch {
            val data = equipmentRepository.getUniqueEquipInfos(uid, lv)
            uniqueEquip.postValue(data)
        }
    }

    suspend fun getUEMaxLv() = equipmentRepository.getUniqueEquipMaxLv()

    //获取装备类型
    suspend fun getTypes() = equipmentRepository.getEquipTypes()

}