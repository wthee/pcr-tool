package cn.wthee.pcrtool.ui.character

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.CharacterHomePageComment
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.db.view.RoomCommentData
import cn.wthee.pcrtool.data.model.FilterCharacter
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
    val homePageCommentList: List<CharacterHomePageComment> = emptyList(),
    val roomCommentList: List<RoomCommentData> = emptyList(),
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

    fun loadData(filter: FilterCharacter) {
        getCharacterInfoList(filter)
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


}

