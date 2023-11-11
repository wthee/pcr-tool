package cn.wthee.pcrtool.ui.character

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.SkillRepository
import cn.wthee.pcrtool.data.db.view.AttackPattern
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
 * 页面状态：角色技能循环
 */
@Immutable
data class CharacterSkillLoopUiState(
    val attackPatternList : List<AttackPattern> = emptyList(),
    val loadingState: LoadingState = LoadingState.Loading
)

/**
 * 角色技能循环 ViewModel
 *
 * @param skillRepository
 *
 */
@HiltViewModel
class CharacterSkillLoopViewModel @Inject constructor(
    private val skillRepository: SkillRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterSkillLoopUiState())
    val uiState: StateFlow<CharacterSkillLoopUiState> = _uiState.asStateFlow()


    fun loadData(unitId: Int){
        getCharacterSkillLoopList(unitId)
    }

    /**
     * 获取角色技能循环
     */
    private fun getCharacterSkillLoopList(unitId: Int) {
        viewModelScope.launch {
            val list = skillRepository.getAttackPattern(unitId)
            _uiState.update {
                it.copy(
                    attackPatternList = list,
                    loadingState = updateLoadingState(list)
                )
            }
        }
    }

}
