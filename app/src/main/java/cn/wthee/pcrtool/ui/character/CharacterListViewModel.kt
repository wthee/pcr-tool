package cn.wthee.pcrtool.ui.character

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.model.getStarCharacterIdList
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.navigation.getData
import cn.wthee.pcrtool.navigation.setData
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.updateLoadingState
import cn.wthee.pcrtool.utils.GsonUtil
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
    val filter: FilterCharacter? = null,
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
     * 获取筛选信息
     */
    fun initFilter() {
        viewModelScope.launch {
            val filterData = getData<String>(NavRoute.FILTER_DATA)
            val filter: FilterCharacter? = GsonUtil.fromJson(filterData)
            val startIdList = getStarCharacterIdList()
            val initFilter = filter ?: FilterCharacter()
            _uiState.update {
                it.copy(
                    filter = initFilter,
                    startIdList = startIdList
                )
            }

            //初始加载
            getCharacterInfoList(initFilter)
        }
    }

    /**
     * 重置筛选
     */
    fun resetFilter() {
        viewModelScope.launch {
            val filter = FilterCharacter()
            val list = unitRepository.getCharacterInfoList(filter, Int.MAX_VALUE)
            setData(NavRoute.FILTER_DATA, null)
            _uiState.update {
                it.copy(
                    filter = filter,
                    characterList = list,
                    loadingState = updateLoadingState(list)
                )
            }
        }
    }

}

