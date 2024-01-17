package cn.wthee.pcrtool.ui.character.profile

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.CharacterHomePageComment
import cn.wthee.pcrtool.data.db.view.CharacterProfileInfo
import cn.wthee.pcrtool.data.db.view.RoomCommentData
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.ui.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：角色简介
 */
@Immutable
data class CharacterProfileUiState(
    //角色基本信息
    val profile: CharacterProfileInfo? = null,
    //主页交流文本
    val homePageCommentList: List<CharacterHomePageComment> = emptyList(),
    //公会小屋交流文本
    val roomCommentList: List<RoomCommentData> = emptyList(),
    val loadingState: LoadingState = LoadingState.Loading
)

/**
 * 角色面板属性 ViewModel
 *
 * @param unitRepository
 *
 */
@HiltViewModel
class CharacterProfileViewModel @Inject constructor(
    private val unitRepository: UnitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val unitId: Int? = savedStateHandle[NavRoute.UNIT_ID]

    private val _uiState = MutableStateFlow(CharacterProfileUiState())
    val uiState: StateFlow<CharacterProfileUiState> = _uiState.asStateFlow()


    init {
        if(unitId != null){
            getCharacter(unitId)
            getHomePageComments(unitId)
            getRoomComments(unitId)
        }
    }

    /**
     * 获取角色基本资料
     *
     * @param unitId 角色编号
     */
    private fun getCharacter(unitId: Int) {
        viewModelScope.launch {
            val data = unitRepository.getProfileInfo(unitId)
            _uiState.update {
                it.copy(
                    profile = data,
                    loadingState =  it.loadingState.isSuccess(data != null)
                )
            }
        }
    }

    /**
     * 获取角色基本资料
     *
     * @param unitId 角色编号
     */
    private fun getHomePageComments(unitId: Int) {
        viewModelScope.launch {
            val data = unitRepository.getHomePageComments(unitId)
            _uiState.update {
                it.copy(
                    homePageCommentList = data
                )
            }
        }
    }

    /**
     * 获取角色小屋对话
     *
     * @param unitId 角色编号
     */
    private fun getRoomComments(unitId: Int) {
        viewModelScope.launch {
            val data = unitRepository.getRoomComments(unitId)
            _uiState.update {
                it.copy(
                    roomCommentList = data
                )
            }
        }
    }

}
