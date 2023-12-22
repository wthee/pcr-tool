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
import cn.wthee.pcrtool.utils.GsonUtil
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：角色列表
 */
@Immutable
data class CharacterListFilterUiState(
    val guildList: List<GuildData> = emptyList(),
    val raceList: List<String> = emptyList(),
    val filter: FilterCharacter = FilterCharacter()
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
    private val filter: FilterCharacter? = GsonUtil.fromJson(savedStateHandle[NavRoute.FILTER_DATA])

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
            _uiState.update {
                it.copy(
                    filter = initFilter
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
            Gson().toJson(filter),
            prev = true
        )
        _uiState.update {
            it.copy(
                filter = filter
            )
        }
    }

}

