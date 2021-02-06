package cn.wthee.pcrtool.ui.tool.equip

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.wthee.pcrtool.data.bean.FilterEquipment
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.db.view.UniqueEquipmentMaxData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * 装备 ViewModel
 *
 * 数据来源 [EquipmentRepository]
 */
class EquipmentViewModel(
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    lateinit var equipments: Flow<PagingData<EquipmentMaxData>>
    var updateEquip = MutableLiveData<Boolean>()
    var reset = MutableLiveData<Boolean>()
    var equipmentCounts = MutableLiveData<Int>()
    var uniqueEquip = MutableLiveData<UniqueEquipmentMaxData>()

    /**
     * 获取装备列表
     */
    fun getEquips(params: FilterEquipment, name: String, reload: Boolean = true) {
        viewModelScope.launch {
            if (!this@EquipmentViewModel::equipments.isInitialized || reload) {
                equipments = Pager(
                    PagingConfig(
                        pageSize = 10,
                        enablePlaceholders = false
                    )
                ) {
                    equipmentRepository.getPagingEquipments(
                        name,
                        params
                    )
                }.flow.cachedIn(viewModelScope)
            }
            equipmentCounts.postValue(
                equipmentRepository.getEquipmentCount(
                    name,
                    params
                )
            )
            updateEquip.postValue(true)
        }
    }


    /**
     * 根据 [uid] 专武等级 [lv]，获取专武信息
     */
    fun getUniqueEquipInfos(uid: Int, lv: Int) {
        viewModelScope.launch {
            val data = equipmentRepository.getUniqueEquipInfo(uid, lv)
            uniqueEquip.postValue(data)
        }
    }

    /**
     * 获取装备类型
     */
    suspend fun getTypes() = equipmentRepository.getEquipTypes()

}
