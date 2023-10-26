package cn.wthee.pcrtool.ui.character

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.db.view.GuildData
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.model.getStarCharacterIdList
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.updateLoadingState
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
data class CharacterListUiState(
    val characterList: List<CharacterInfo>? = null,
    val guildList: List<GuildData> = emptyList(),
    val raceList: List<String> = emptyList(),
    val filterCharacter: FilterCharacter = FilterCharacter(),
    //收藏的角色编号
    val startIdList: ArrayList<Int> = arrayListOf(),
    val loadingState: LoadingState = LoadingState.Loading
)

/**
 * 角色列表 ViewModel
 *
 * @param unitRepository
 */
@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val unitRepository: UnitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterListUiState())
    val uiState: StateFlow<CharacterListUiState> = _uiState.asStateFlow()

    init {
        getGuilds()
        getRaces()
        initFilter()
    }


    /**
     * 获取角色基本信息列表
     *
     * @param filter 角色筛选
     */
    private fun getCharacterInfoList(filter: FilterCharacter) {
        viewModelScope.launch {
            val list = unitRepository.getCharacterInfoList(filter, Int.MAX_VALUE)
            _uiState.update {
                it.copy(
                    characterList = list,
                    loadingState = updateLoadingState(list)
                )
            }
        }
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
            val startIdList = getStarCharacterIdList()
            val filter = FilterCharacter()
            _uiState.update {
                it.copy(
                    filterCharacter = filter,
                    startIdList = startIdList
                )
            }
            //初始加载
            getCharacterInfoList(filter)
        }
    }

    /**
     * 更新筛选条件
     */
    fun updateFilter(filter: FilterCharacter) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    filterCharacter = filter
                )
            }
            //重新加载
            getCharacterInfoList(filter)
        }
    }

    /**
     * 更新收藏id
     */
    fun reloadStarIdList() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    startIdList = getStarCharacterIdList()
                )
            }
        }
    }
}

