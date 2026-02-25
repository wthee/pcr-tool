package cn.wthee.pcrtool.ui.home.uniqueequip

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.view.UniqueEquipBasicData
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
    val uniqueEquipCount: String = "0",
    //专用装备1列表
    val uniqueEquipList1: List<UniqueEquipBasicData>? = null,
    //专用装备2列表
    val uniqueEquipList2: List<UniqueEquipBasicData>? = null,
    //专用装备1sp列表
    val uniqueEquipSpList1: List<UniqueEquipBasicData>? = null,
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
        if (limit > 0) {
            //初始加载占位
            if (_uiState.value.uniqueEquipList1 == null) {
                val initList = arrayListOf<UniqueEquipBasicData>()
                for (i in 1..limit) {
                    initList.add(UniqueEquipBasicData())
                }
                _uiState.update {
                    it.copy(
                        uniqueEquipList1 = initList
                    )
                }
            }
            getUniqueEquipInfoList(limit)
        }
    }

    /**
     * 获取专用装备列表
     */
    private fun getUniqueEquipInfoList(limit: Int) {
        viewModelScope.launch {
            val filterList1 = equipmentRepository.getUniqueEquipList("", 1)
            val filterList2 = equipmentRepository.getUniqueEquipList("", 2)
            val filterList3 = equipmentRepository.getUniqueEquipList("", 3)

            _uiState.update {
                it.copy(
                    uniqueEquipList1 = filterList1.subList(0, min(limit, filterList1.size)),
                    uniqueEquipList2 = filterList2.subList(0, min(limit, filterList2.size)),
                    uniqueEquipSpList1 = filterList3.subList(0, min(limit, filterList3.size)),
                    uniqueEquipCount =if (filterList3.isNotEmpty()) {
                        "${filterList1.size} · ${filterList2.size} · ${filterList3.size}"
                    }else if (filterList2.isNotEmpty()) {
                        "${filterList1.size} · ${filterList2.size}"
                    } else {
                        "${filterList1.size}"
                    }
                )
            }
        }
    }

}