package cn.wthee.pcrtool.ui.tool.clan

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.ClanBattleRepository
import cn.wthee.pcrtool.data.db.view.ClanBattleInfo
import cn.wthee.pcrtool.ui.LoadState
import cn.wthee.pcrtool.ui.updateLoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 页面状态：公会战列表
 */
@Immutable
data class ClanBattleListUiState(
    //公会战列表
    val clanBattleList: List<ClanBattleInfo>? = null,
    val loadState: LoadState = LoadState.Loading
)

/**
 * 公会战列表 ViewModel
 */
@HiltViewModel
class ClanBattleListViewModel @Inject constructor(
    private val clanBattleRepository: ClanBattleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClanBattleListUiState())
    val uiState: StateFlow<ClanBattleListUiState> = _uiState.asStateFlow()

    init {
        getClanBattleList()
    }

    /**
     * 获取全部公会战记录
     */
    private fun getClanBattleList() {
        viewModelScope.launch {
            val clanBattleList = clanBattleRepository.getClanBattleList(0, 2)
            _uiState.update {
                it.copy(
                    clanBattleList = clanBattleList,
                    loadState = updateLoadState(clanBattleList)
                )
            }
        }
    }

}
