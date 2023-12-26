package cn.wthee.pcrtool.ui.home.uniqueequip

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.utils.ImageRequestHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min

/**
 * 页面状态：专用装备纵览
 */
@Immutable
data class UniqueEquipSectionUiState(
    //装备数量
    val uniqueEquipCount: String = "",
    //专用装备1列表
    val uniqueEquipIdList1: List<Int>? = null,
    //专用装备2列表
    val uniqueEquipIdList2: List<Int>? = null,
)

/**
 * 专用装备纵览
 */
@HiltViewModel
class UniqueEquipSectionViewModel @Inject constructor(
    private val equipmentRepository: EquipmentRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UniqueEquipSectionUiState())
    val uiState: StateFlow<UniqueEquipSectionUiState> = _uiState.asStateFlow()


    fun loadData(limit: Int) {
        if (_uiState.value.uniqueEquipIdList1 == null) {
            val initList = arrayListOf<Int>()
            for (i in 1..limit) {
                initList.add(ImageRequestHelper.UNKNOWN_EQUIP_ID)
            }
            _uiState.update {
                it.copy(
                    uniqueEquipIdList1 = initList
                )
            }
        }
        getUniqueEquipCount()
        getUniqueEquipInfoList(limit)
    }

    /**
     * 获取专用装备数量
     */
    private fun getUniqueEquipCount() {
        viewModelScope.launch {
            val count = equipmentRepository.getUniqueEquipCount()
            _uiState.update {
                it.copy(
                    uniqueEquipCount = count
                )
            }
        }
    }

    /**
     * 获取专用装备列表
     */
    private fun getUniqueEquipInfoList(limit: Int) {
        viewModelScope.launch {
            val filterList1 = equipmentRepository.getUniqueEquipList("", 1)?.map { it.equipId }
            val filterList2 = equipmentRepository.getUniqueEquipList("", 2)?.map { it.equipId }

            _uiState.update {
                it.copy(
                    uniqueEquipIdList1 = filterList1?.subList(0, min(limit, filterList1.size)),
                    uniqueEquipIdList2 = filterList2?.subList(0, min(limit, filterList2.size)),
                )
            }
        }
    }

}