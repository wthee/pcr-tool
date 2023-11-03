package cn.wthee.pcrtool.ui.tool.extraequip

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.ExtraEquipmentRepository
import cn.wthee.pcrtool.data.db.view.ExtraEquipQuestData
import cn.wthee.pcrtool.data.db.view.ExtraEquipSubRewardData
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.updateLoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：ex装备掉落详情
 */
@Immutable
data class ExtraEquipDropUiState(
    val dropList: List<ExtraEquipQuestData>? = emptyList(),
    val subRewardListMap: HashMap<Int, List<ExtraEquipSubRewardData>>? = null,
    val loadingState: LoadingState = LoadingState.Loading
)

/**
 * ex装备掉落详情 ViewModel
 */
@HiltViewModel
class ExtraEquipDropViewModel @Inject constructor(
    private val extraEquipmentRepository: ExtraEquipmentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val equipId: Int? = savedStateHandle[NavRoute.EQUIP_ID]

    private val _uiState = MutableStateFlow(ExtraEquipDropUiState())
    val uiState: StateFlow<ExtraEquipDropUiState> = _uiState.asStateFlow()

    init {
        if (equipId != null) {
            getExtraDropQuestList(equipId)
        }
    }


    /**
     * 装备掉落信息
     */
    private fun getExtraDropQuestList(equipId: Int) {
        viewModelScope.launch {
            val list = extraEquipmentRepository.getDropQuestList(equipId)
            val map = hashMapOf<Int, List<ExtraEquipSubRewardData>>()
            list?.forEach {
                val subRewardList = extraEquipmentRepository.getSubRewardList(it.travelQuestId)
                map[it.travelQuestId] = subRewardList
            }
            _uiState.update {
                it.copy(
                    dropList = list,
                    subRewardListMap = map,
                    loadingState = updateLoadingState(list)
                )
            }
        }
    }
}
