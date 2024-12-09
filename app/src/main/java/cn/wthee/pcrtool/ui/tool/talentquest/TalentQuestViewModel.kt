package cn.wthee.pcrtool.ui.tool.talentquest

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.QuestRepository
import cn.wthee.pcrtool.data.db.view.TalentQuestData
import cn.wthee.pcrtool.data.enums.TalentType
import cn.wthee.pcrtool.ui.LoadState
import cn.wthee.pcrtool.ui.updateLoadState
import cn.wthee.pcrtool.utils.LogReportUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 页面状态：深域关卡
 */
@Immutable
data class TalentQuestUiState(
    val questDataList: List<TalentQuestData>? = null,
    val talentType: TalentType = TalentType.FIRE,
    val openTalentDialog: Boolean = false,
    val loadState: LoadState = LoadState.Loading
)

/**
 * 深域关卡 ViewModel
 */
@HiltViewModel
class TalentQuestViewModel @Inject constructor(
    private val questRepository: QuestRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TalentQuestUiState())
    val uiState: StateFlow<TalentQuestUiState> = _uiState.asStateFlow()

    init {
        getTalentQuestList(TalentType.FIRE.type)
    }

    /**
     * 获取深域关卡奖励
     */
    private fun getTalentQuestList(talentType: Int) {
        viewModelScope.launch {
            try {
                val list = questRepository.getTalentQuestList(talentType)
                _uiState.update {
                    it.copy(
                        questDataList = list,
                        talentType = TalentType.getByType(talentType),
                        loadState = updateLoadState(list)
                    )
                }
            } catch (e: Exception) {
                LogReportUtil.upload(e, "getTalentQuestList")
            }
        }
    }


    /**
     * 切换天赋类型
     * index + 1 对应天赋类型
     */
    fun changeTalentSelect(index: Int) {
        getTalentQuestList(talentType = index + 1)
    }

    /**
     * 弹窗状态更新
     */
    fun changeTalentDialog(openDialog: Boolean) {
        _uiState.update {
            it.copy(
                openTalentDialog = openDialog
            )
        }
    }
}
