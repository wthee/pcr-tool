package cn.wthee.pcrtool.ui.tool.extraequip

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.ExtraEquipmentRepository
import cn.wthee.pcrtool.data.db.view.ExtraEquipmentBasicInfo
import cn.wthee.pcrtool.data.model.FilterExtraEquipment
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.navigation.getData
import cn.wthee.pcrtool.navigation.setData
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.updateLoadingState
import cn.wthee.pcrtool.utils.JsonUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：ex装备列表
 */
@Immutable
data class ExtraEquipListUiState(
    val equipList: List<ExtraEquipmentBasicInfo>? = null,
    val filter: FilterExtraEquipment? = null,
    //收藏的编号
    val favoriteIdList: List<Int> = emptyList(),
    val loadingState: LoadingState = LoadingState.Loading
)

/**
 * ex装备列表 ViewModel
 */
@HiltViewModel
class ExtraEquipListViewModel @Inject constructor(
    private val extraEquipmentRepository: ExtraEquipmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExtraEquipListUiState())
    val uiState: StateFlow<ExtraEquipListUiState> = _uiState.asStateFlow()


    /**
     * 获取装备基本信息列表
     *
     * @param filter 装备筛选
     */
    private fun getEquipInfoList(filter: FilterExtraEquipment) {
        viewModelScope.launch {
            val list = extraEquipmentRepository.getEquipmentList(filter, Int.MAX_VALUE)
            _uiState.update {
                it.copy(
                    equipList = list,
                    loadingState = updateLoadingState(list)
                )
            }
        }
    }

    /**
     * 获取筛选信息
     */
    fun initFilter() {
        viewModelScope.launch {
            val filterData = getData<String>(NavRoute.FILTER_DATA)
            val filter: FilterExtraEquipment? = JsonUtil.fromJson(filterData)
            val favoriteIdList = FilterExtraEquipment.getFavoriteIdList()
            val initFilter = filter ?: FilterExtraEquipment()
            _uiState.update {
                it.copy(
                    filter = initFilter,
                    favoriteIdList = favoriteIdList
                )
            }

            //初始加载
            getEquipInfoList(initFilter)
        }
    }

    /**
     * 重置筛选
     */
    fun resetFilter() {
        viewModelScope.launch {
            val filter = FilterExtraEquipment()
            val list = extraEquipmentRepository.getEquipmentList(filter, Int.MAX_VALUE)
            setData(NavRoute.FILTER_DATA, null)
            _uiState.update {
                it.copy(
                    filter = filter,
                    equipList = list,
                    loadingState = updateLoadingState(list)
                )
            }
        }
    }

}

