package cn.wthee.pcrtool.ui.tool.clan

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.ClanBattleRepository
import cn.wthee.pcrtool.data.db.view.ClanBattleInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 页面状态：公会战纵览item
 */
@Immutable
data class ClanBattleOverviewUiState(
    //公会战详情
    val clanInfoMap: HashMap<Int, ClanBattleInfo?> = hashMapOf()
)

/**
 * 公会战纵览item ViewModel
 */
@HiltViewModel
class ClanBattleOverviewViewModel @Inject constructor(
    private val clanBattleRepository: ClanBattleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClanBattleOverviewUiState())
    val uiState: StateFlow<ClanBattleOverviewUiState> = _uiState.asStateFlow()


    fun loadData(clanId: Int = 0, phase: Int = 2) {
        getClanBattleInfo(clanId, phase)
    }

    /**
     * 获取全部公会战记录
     */
    private fun getClanBattleInfo(clanId: Int, phase: Int) {
        viewModelScope.launch {
            val clanList = clanBattleRepository.getClanBattleList(clanId, phase)
            if (clanList.isNotEmpty()) {
                _uiState.update {
                    val map = it.clanInfoMap
                    map[clanId] = clanList[0]
                    it.copy(
                        clanInfoMap = map
                    )
                }
            }
        }
    }
}
