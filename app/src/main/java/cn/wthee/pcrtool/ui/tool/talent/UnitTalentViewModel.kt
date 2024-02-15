package cn.wthee.pcrtool.ui.tool.talent

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.TalentData
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
    //角色天赋列表
    val unitTalentList: List<TalentData>? = null,
    val loadState: LoadState = LoadState.Loading
)

/**
 * 角色天赋 ViewModel
 */
@HiltViewModel
class UnitTalentViewModel @Inject constructor(
    private val unitRepository: UnitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UnitTalentListUiState())
    val uiState: StateFlow<UnitTalentListUiState> = _uiState.asStateFlow()

    init {
        getUnitTalentList()
    }

    /**
     * 获取角色天赋记录
     */
    private fun getUnitTalentList() {
        viewModelScope.launch {
            try {
                val list = unitRepository.getTalentIdList(0)

                _uiState.update {
                    it.copy(
                        unitTalentList = list,
                        loadState = updateLoadState(list)
                    )
                }
            } catch (e: Exception) {
                LogReportUtil.upload(e, "getUnitTalentList")
            }
        }
    }
}
