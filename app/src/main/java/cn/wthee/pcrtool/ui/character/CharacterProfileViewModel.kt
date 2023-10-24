package cn.wthee.pcrtool.ui.character

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.CharacterHomePageComment
import cn.wthee.pcrtool.data.db.view.CharacterProfileInfo
import cn.wthee.pcrtool.data.db.view.RoomCommentData
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
    val profile: CharacterProfileInfo? = null,
    val homePageCommentList: List<CharacterHomePageComment> = emptyList(),
    val roomCommentList: List<RoomCommentData> = emptyList(),
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
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterProfileUiState())
    val uiState: StateFlow<CharacterProfileUiState> = _uiState.asStateFlow()


    fun loadData(unitId: Int) {
        getCharacter(unitId)
        getHomePageComments(unitId)
        getRoomComments(unitId)
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
                    profile = data
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
