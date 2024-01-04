package cn.wthee.pcrtool.ui.equip

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.view.EquipmentBasicInfo
import cn.wthee.pcrtool.data.model.FilterEquip
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.navigation.getData
import cn.wthee.pcrtool.navigation.setData
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.updateLoadingState
import cn.wthee.pcrtool.utils.JsonUtil
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.getString
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
 * 页面状态：装备列表
 */
@Immutable
data class EquipListUiState(
    val equipList: List<EquipmentBasicInfo>? = null,
    val filter: FilterEquip? = null,
    //收藏的编号
    val starIdList: List<Int> = emptyList(),
    //搜索模式
    val searchEquipMode: Boolean = false,
    //搜索装备编号
    val searchEquipIdList: List<Int> = emptyList(),
    val openSearchDialog: Boolean = false,
    val loadingState: LoadingState = LoadingState.Loading
)

/**
 * 装备列表 ViewModel
 */
@HiltViewModel
class EquipListViewModel @Inject constructor(
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {
    //装备列表最大搜索数
    private val maxCount = 5

    private val _uiState = MutableStateFlow(EquipListUiState())
    val uiState: StateFlow<EquipListUiState> = _uiState.asStateFlow()


    /**
     * 获取装备基本信息列表
     *
     * @param filter 装备筛选
     */
    private fun getEquipInfoList(filter: FilterEquip) {
        viewModelScope.launch {
            val list = equipmentRepository.getEquipmentList(filter, Int.MAX_VALUE)
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
            val filter: FilterEquip? = JsonUtil.fromJson(filterData)
            val starIdList = FilterEquip.getStarIdList()
            val initFilter = filter ?: FilterEquip()
            _uiState.update {
                it.copy(
                    filter = initFilter,
                    starIdList = starIdList
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
            val filter = FilterEquip()
            val list = equipmentRepository.getEquipmentList(filter, Int.MAX_VALUE)
            setData(NavRoute.FILTER_DATA, null)
            _uiState.update {
                it.copy(
                    filter = filter,
                    equipList = list,
                    openSearchDialog = false,
                    searchEquipMode = false,
                    searchEquipIdList = emptyList(),
                    loadingState = updateLoadingState(list)
                )
            }
        }
    }

    /**
     * 更新筛选条件
     */
    private fun updateFilter(filter: FilterEquip) {
        setData(
            NavRoute.FILTER_DATA,
            Json.encodeToString(filter)
        )
        _uiState.update {
            it.copy(
                filter = filter
            )
        }
        getEquipInfoList(filter)
    }

    /**
     * 切换搜索模式
     */
    fun changeSearchMode() {
        viewModelScope.launch {
            _uiState.update {
                //更新筛选条件
                it.filter?.copy()?.let { filter ->
                    filter.craft = if (it.searchEquipMode) 1 else 0
                    updateFilter(filter = filter)
                }

                it.copy(
                    searchEquipMode = !it.searchEquipMode
                )
            }
        }
    }

    /**
     * 选中或取消选择装备
     */
    fun selectEquip(equipId: Int) {
        viewModelScope.launch {
            val newList = arrayListOf<Int>()
            newList.addAll(_uiState.value.searchEquipIdList)
            if (newList.contains(equipId)) {
                newList.remove(equipId)
                //无选择的装备，关闭弹窗
                if (newList.isEmpty()) {
                    changeSearchDialog(false)
                }
            } else {
                if (newList.size >= maxCount) {
                    ToastUtil.short(
                        getString(
                            R.string.equip_max_select_count,
                            maxCount
                        )
                    )
                    return@launch
                } else {
                    newList.add(equipId)
                }
            }

            _uiState.update {
                it.copy(
                    searchEquipIdList = newList
                )
            }
        }
    }


    /**
     * 切换搜索模式
     */
    fun changeSearchDialog(open: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    openSearchDialog = open
                )
            }
        }
    }
}

