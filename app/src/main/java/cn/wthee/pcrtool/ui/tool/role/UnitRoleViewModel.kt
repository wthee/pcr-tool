package cn.wthee.pcrtool.ui.tool.role

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
 * 页面状态：角色职能
 */
@Immutable
data class UnitRoleListUiState(
    val showAllType: Boolean = true,
    val selectedUnitId: Int = 0,
    val roleType: Int = 0,
    //角色职能列表
    val unitRoleList: List<CharacterTalentRoleInfo>? = null,
    val loadState: LoadState = LoadState.Loading
)

/**
 * 角色职能 ViewModel
 */
@HiltViewModel
class UnitRoleViewModel @Inject constructor(
    private val unitRepository: UnitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    //角色id，职能类型
    private val selectedUnitId: Int = savedStateHandle[NavRoute.UNIT_ID] ?: 0
    private val roleType: Int = savedStateHandle[NavRoute.ROLE_TYPE] ?: 0

    private val _uiState = MutableStateFlow(UnitRoleListUiState())
    val uiState: StateFlow<UnitRoleListUiState> = _uiState.asStateFlow()

    init {
        getUnitRoleList(selectedUnitId, roleType)
    }

    /**
     * 获取角色职能记录
     *
     * @param selectedUnitId 选中的角色id
     * @param roleType 职能类型
     */
    private fun getUnitRoleList(selectedUnitId: Int, roleType: Int) {
        viewModelScope.launch {
            try {
                val list = unitRepository.getRoleIdList(0, roleType)

                _uiState.update {
                    it.copy(
                        unitRoleList = list,
                        roleType = roleType,
                        loadState = updateLoadState(list),
                        showAllType = selectedUnitId == 0,
                        selectedUnitId = selectedUnitId
                    )
                }
            } catch (e: Exception) {
                LogReportUtil.upload(e, "getUnitRoleList")
            }
        }
    }
}
