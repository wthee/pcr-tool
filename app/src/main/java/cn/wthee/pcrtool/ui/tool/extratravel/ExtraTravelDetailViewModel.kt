package cn.wthee.pcrtool.ui.tool.extratravel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.ExtraEquipmentRepository
import cn.wthee.pcrtool.data.db.view.ExtraEquipQuestData
import cn.wthee.pcrtool.data.db.view.ExtraEquipSubRewardData
import cn.wthee.pcrtool.navigation.NavRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：ex冒险区域
 */
@Immutable
data class ExtraTravelDetailUiState(
    val questData: ExtraEquipQuestData? = null,
    val subRewardList: List<ExtraEquipSubRewardData>? = null
)


/**
 * ex冒险区域 ViewModel
 */
@HiltViewModel
class ExtraTravelDetailViewModel @Inject constructor(
    private val extraEquipmentRepository: ExtraEquipmentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val questId: Int? = savedStateHandle[NavRoute.TRAVEL_QUEST_ID]

    private val _uiState = MutableStateFlow(ExtraTravelDetailUiState())
    val uiState: StateFlow<ExtraTravelDetailUiState> = _uiState.asStateFlow()

    init {
        if (questId != null) {
            getTravelQuest(questId)
            getSubRewardList(questId)
        }
    }

    fun loadData(questId: Int) {
        getTravelQuest(questId)
        getSubRewardList(questId)
    }


    /**
     * 次要掉落信息
     */
    private fun getSubRewardList(questId: Int) {
        viewModelScope.launch {
            val subRewardList = extraEquipmentRepository.getSubRewardList(questId)
            _uiState.update {
                it.copy(
                    subRewardList = subRewardList
                )
            }
        }
    }

    /**
     * 冒险区域详情
     */
    private fun getTravelQuest(questId: Int) {
        viewModelScope.launch {
            val questData = extraEquipmentRepository.getTravelQuest(questId)
            _uiState.update {
                it.copy(
                    questData = questData
                )
            }
        }
    }
}
