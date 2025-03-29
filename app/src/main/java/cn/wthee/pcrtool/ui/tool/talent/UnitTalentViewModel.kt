package cn.wthee.pcrtool.ui.tool.talent

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.CharacterTalentRoleInfo
import cn.wthee.pcrtool.navigation.NavRoute
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
 * 页面状态：角色天赋
 */
@Immutable
data class UnitTalentListUiState(
    val showAllType: Boolean = true,
    val selectedUnitId: Int = 0,
    val talentType: Int = 0,
    //角色天赋列表
    val unitTalentList: List<CharacterTalentRoleInfo>? = null,
    val loadState: LoadState = LoadState.Loading
)

/**
 * 角色天赋 ViewModel
 */
@HiltViewModel
class UnitTalentViewModel @Inject constructor(
    private val unitRepository: UnitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    //角色id，天赋类型
    private val selectedUnitId: Int = savedStateHandle[NavRoute.UNIT_ID] ?: 0
    private val talentType: Int = savedStateHandle[NavRoute.TALENT_TYPE] ?: 0

    private val _uiState = MutableStateFlow(UnitTalentListUiState())
    val uiState: StateFlow<UnitTalentListUiState> = _uiState.asStateFlow()

    init {
        getUnitTalentList(selectedUnitId, talentType)
    }

    /**
     * 获取角色天赋记录
     *
     * @param selectedUnitId 选中的角色id
     * @param talentType 天赋类型
     */
    private fun getUnitTalentList(selectedUnitId: Int, talentType: Int) {
        viewModelScope.launch {
            try {
                val list = unitRepository.getTalentIdList(0, talentType)

                _uiState.update {
                    it.copy(
                        unitTalentList = list,
                        talentType = talentType,
                        loadState = updateLoadState(list),
                        showAllType = selectedUnitId == 0,
                        selectedUnitId = selectedUnitId
                    )
                }
            } catch (e: Exception) {
                LogReportUtil.upload(e, "getUnitTalentList")
            }
        }
    }
}
