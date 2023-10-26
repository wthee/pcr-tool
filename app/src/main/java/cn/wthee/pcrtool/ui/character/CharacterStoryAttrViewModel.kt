package cn.wthee.pcrtool.ui.character

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.CharacterStoryAttr
import cn.wthee.pcrtool.navigation.NavRoute
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
 * 页面状态：角色剧情属性
 */
@Immutable
data class CharacterStoryUiState(
    val storyAttrList : List<CharacterStoryAttr>? = emptyList(),
    val loadingState: LoadingState = LoadingState.Loading
)

/**
 * 角色剧情属性 ViewModel
 *
 * @param unitRepository
 *
 */
@HiltViewModel
class CharacterStoryViewModel @Inject constructor(
    private val unitRepository: UnitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val unitId: Int? = savedStateHandle[NavRoute.UNIT_ID]

    private val _uiState = MutableStateFlow(CharacterStoryUiState())
    val uiState: StateFlow<CharacterStoryUiState> = _uiState.asStateFlow()

    init {
        if (unitId != null) {
            getCharacterStoryAttrList(unitId)
        }
    }

    /**
     * 获取角色剧情属性
     */
    private fun getCharacterStoryAttrList(unitId: Int) {
        viewModelScope.launch {
            val list = unitRepository.getCharacterStoryAttrList(unitId)
            _uiState.update {
                it.copy(
                    storyAttrList = list,
                    loadingState = updateLoadingState(list)
                )
            }
        }
    }

}
