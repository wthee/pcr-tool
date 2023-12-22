package cn.wthee.pcrtool.ui.equip.filter

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.model.FilterEquip
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.navigation.setData
import cn.wthee.pcrtool.utils.GsonUtil
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：装备列表
 */
@Immutable
data class EquipListFilterUiState(
    val filter: FilterEquip = FilterEquip(),
    //rank颜色数量
    val colorNum: Int = 0
)

/**
 * 装备列表 ViewModel
 */
@HiltViewModel
class EquipListFilterViewModel @Inject constructor(
    private val equipmentRepository: EquipmentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val filter: FilterEquip? = GsonUtil.fromJson(savedStateHandle[NavRoute.FILTER_DATA])

    private val _uiState = MutableStateFlow(EquipListFilterUiState())
    val uiState: StateFlow<EquipListFilterUiState> = _uiState.asStateFlow()

    init {
        initFilter()
        getEquipColorNum()
    }


    /**
     * 获取筛选信息
     */
    private fun initFilter() {
        viewModelScope.launch {
            val initFilter = filter ?: FilterEquip()
            _uiState.update {
                it.copy(
                    filter = initFilter
                )
            }
        }
    }

    /**
     * 获取装备颜色种类数
     */
    private fun getEquipColorNum() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    colorNum = equipmentRepository.getEquipColorNum()
                )
            }
        }
    }

    /**
     * 更新筛选条件
     */
    fun updateFilter(filter: FilterEquip) {
        setData(
            NavRoute.FILTER_DATA,
            Gson().toJson(filter),
            prev = true
        )
    }

}

