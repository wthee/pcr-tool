package cn.wthee.pcrtool.ui.character.filter

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.GuildData
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.navigation.setData
import cn.wthee.pcrtool.utils.JsonUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * 页面状态：角色列表
 */
@Immutable
data class CharacterListFilterUiState(
    // 公会信息
    val guildList: List<GuildData> = emptyList(),
    // 种族信息
    val raceList: List<String> = emptyList(),
    val filter: FilterCharacter = FilterCharacter(),
    // 是否有天赋类型
    val hasTalent: Boolean = false
)

/**
 * 角色列表 ViewModel
 *
 * @param unitRepository
 */
@HiltViewModel
class CharacterListFilterViewModel @Inject constructor(
    private val unitRepository: UnitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val filter: FilterCharacter? = JsonUtil.fromJson(savedStateHandle[NavRoute.FILTER_DATA])

    private val _uiState = MutableStateFlow(CharacterListFilterUiState())
    val uiState: StateFlow<CharacterListFilterUiState> = _uiState.asStateFlow()

    init {
        getGuilds()
        getRaces()
        initFilter()
    }

    /**
     * 公会信息
     */
    private fun getGuilds() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    guildList = unitRepository.getGuilds()
                )
            }
        }
    }

    /**
     * 种族信息
     */
    private fun getRaces() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    raceList = unitRepository.getRaces()
                )
            }
        }
    }

    /**
     * 获取筛选信息
     */
    private fun initFilter() {
        viewModelScope.launch {
            val initFilter = filter ?: FilterCharacter()
            val hasTalent = unitRepository.getTalentIdList(100101).isNotEmpty()
            _uiState.update {
                it.copy(
                    filter = initFilter,
                    hasTalent = hasTalent
                )
            }
        }
    }

    /**
     * 更新筛选条件
     */
    fun updateFilter(filter: FilterCharacter) {
        setData(
            NavRoute.FILTER_DATA,
            Json.encodeToString(filter),
            prev = true
        )
        _uiState.update {
            it.copy(
                filter = filter
            )
        }
    }

}

