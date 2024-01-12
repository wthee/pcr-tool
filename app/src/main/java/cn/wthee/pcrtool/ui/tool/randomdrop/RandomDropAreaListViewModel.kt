package cn.wthee.pcrtool.ui.tool.randomdrop

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.repository.QuestRepository
import cn.wthee.pcrtool.data.model.RandomEquipDropArea
import cn.wthee.pcrtool.data.network.ApiRepository
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.updateLoadingState
import cn.wthee.pcrtool.utils.LogReportUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：随机掉落
 */
@Immutable
data class RandomDropAreaListUiState(
    //额外掉落
    val randomDropList: List<RandomEquipDropArea>? = null,
    val loadingState: LoadingState = LoadingState.Loading
)


/**
 * 随机掉落 ViewModel
 *
 * @param questRepository
 */
@HiltViewModel
class RandomDropAreaListViewModel @Inject constructor(
    private val questRepository: QuestRepository,
    private val apiRepository: ApiRepository,
    private val equipmentRepository: EquipmentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val equipId: Int? = savedStateHandle[NavRoute.EQUIP_ID]

    private val _uiState = MutableStateFlow(RandomDropAreaListUiState())
    val uiState: StateFlow<RandomDropAreaListUiState> = _uiState.asStateFlow()

    init {
        if (equipId != null) {
            getDropAreaList(equipId)
        }
    }

    /**
     * 获取掉落地区信息
     */
    private fun getDropAreaList(equipId: Int) {
        viewModelScope.launch {
            try {
                val response = apiRepository.getEquipArea(equipId)
                if (response.data != null) {
                    val maxArea = equipmentRepository.getMaxArea() % 100
                    val filterList = response.data!!.filter { areaData -> areaData.area <= maxArea }

                    _uiState.update {
                        it.copy(
                            randomDropList = filterList,
                            loadingState = updateLoadingState(filterList)
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            loadingState = LoadingState.Error
                        )
                    }
                }

            } catch (e: Exception) {
                LogReportUtil.upload(e, "getDropAreaList")
            }
        }
    }
}
