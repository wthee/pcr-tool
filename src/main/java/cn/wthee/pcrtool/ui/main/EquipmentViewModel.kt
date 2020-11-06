package cn.wthee.pcrtool.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.data.EquipmentRepository
import cn.wthee.pcrtool.database.view.EquipmentMaxData
import cn.wthee.pcrtool.database.view.UniqueEquipmentMaxData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class EquipmentViewModel(
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    var equipments = Pager(
        PagingConfig(
            pageSize = 30,
            enablePlaceholders = true,
            maxSize = 200
        )
    ) {
        equipmentRepository.getPagingEquipments("")
    }.flow

    var equipmentCounts = MutableLiveData<Int>()
    var uniqueEquip = MutableLiveData<UniqueEquipmentMaxData>()

    //获取装备列表
    suspend fun getEquips(asc: Boolean, name: String) {
        equipments = getPageEquips(asc, name)
    }

    private suspend fun getPageEquips(
        asc: Boolean,
        name: String
    ): Flow<PagingData<EquipmentMaxData>> {
        val equips = Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            equipmentRepository.getPagingEquipments(name)
        }.flow

        equips.collectLatest {
            it.filterSync {
                if (!EquipmentListFragment.equipFilterParams.all) {
                    //过滤非收藏角色
                    if (!MainActivity.sp.getBoolean(it.equipmentId.toString(), false)) {
                        return@filterSync false
                    }
                }
                //种类筛选
                if (EquipmentListFragment.equipFilterParams.type != "全部") {
                    if (EquipmentListFragment.equipFilterParams.type != it.type) {
                        return@filterSync false
                    }
                }
                return@filterSync true
            }
        }
        return equips
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
