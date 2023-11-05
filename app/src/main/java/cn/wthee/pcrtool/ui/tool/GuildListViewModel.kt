package cn.wthee.pcrtool.ui.tool

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.GuildAllMember
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.updateLoadingState
import cn.wthee.pcrtool.utils.getString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：角色公会
 */
@Immutable
data class GuildListUiState(
    val guildList: List<GuildAllMember> = emptyList(),
    val loadingState: LoadingState = LoadingState.Loading
)

/**
 * 公会信息 ViewModel
 */
@HiltViewModel
class GuildListViewModel @Inject constructor(
    private val unitRepository: UnitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuildListUiState())
    val uiState: StateFlow<GuildListUiState> = _uiState.asStateFlow()

    init {
        getGuilds()
    }

    /**
     * 获取公会
     */
    private fun getGuilds() {
        viewModelScope.launch {
            val data = unitRepository.getAllGuildMembers()
            val list = ArrayList(data)
            //无公会成员
            val noGuildData = unitRepository.getNoGuildMembers()
            noGuildData?.let {
                list.add(
                    GuildAllMember(
                        guildId = 999,
                        guildName = getString(R.string.no_guild),
                        unitIds = it.unitIds,
                        unitNames = it.unitNames
                    )
                )
            }

            _uiState.update {
                it.copy(
                    guildList = list,
                    loadingState = updateLoadingState(list)
                )
            }
        }
    }

}
