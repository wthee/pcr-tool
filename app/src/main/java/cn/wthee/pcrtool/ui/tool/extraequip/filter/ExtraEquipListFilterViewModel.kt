package cn.wthee.pcrtool.ui.tool.extraequip.filter

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.ExtraEquipmentRepository
import cn.wthee.pcrtool.data.db.view.ExtraEquipCategoryData
import cn.wthee.pcrtool.data.model.FilterExtraEquipment
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.navigation.setData
import cn.wthee.pcrtool.utils.JsonUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * 页面状态：ex装备列表
 */
@Immutable
data class ExtraEquipListFilterUiState(
    val filter: FilterExtraEquipment = FilterExtraEquipment(),
    //类型
    val equipCategoryList: List<ExtraEquipCategoryData> = emptyList(),
    //颜色类型
    val colorNum: Int = 0
)

/**
 * ex装备列表 ViewModel
 */
@HiltViewModel
class ExtraEquipListFilterViewModel @Inject constructor(
    private val extraEquipmentRepository: ExtraEquipmentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val filter: FilterExtraEquipment? =
        JsonUtil.fromJson(savedStateHandle[NavRoute.FILTER_DATA])

    private val _uiState = MutableStateFlow(ExtraEquipListFilterUiState())
    val uiState: StateFlow<ExtraEquipListFilterUiState> = _uiState.asStateFlow()

    init {
        initFilter()
        getExtraEquipCategoryList()
        getExtraEquipColorNum()
    }


    /**
     * 获取筛选信息
     */
    private fun initFilter() {
        viewModelScope.launch {
            val initFilter = filter ?: FilterExtraEquipment()
            _uiState.update {
                it.copy(
                    filter = initFilter
                )
            }
        }
    }

    /**
     * 获取装备类别
     */
    private fun getExtraEquipCategoryList() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    equipCategoryList = extraEquipmentRepository.getEquipCategoryList()
                )
            }
        }
    }

    /**
     * 获取装备颜色种类数
     */
    private fun getExtraEquipColorNum() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    colorNum = extraEquipmentRepository.getEquipColorNum()
                )
            }
        }
    }

    /**
     * 更新筛选条件
     */
    fun updateFilter(filter: FilterExtraEquipment) {
        setData(
            NavRoute.FILTER_DATA,
            Json.encodeToString(filter),
            prev = true
        )
    }

}

