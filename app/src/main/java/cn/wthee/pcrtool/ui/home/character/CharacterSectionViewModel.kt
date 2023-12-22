package cn.wthee.pcrtool.ui.home.character

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.utils.LogReportUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：角色纵览
 */
@Immutable
data class CharacterSectionUiState(
    //角色数量
    val characterCount: String = "0",
    //角色列表
    val characterList: List<CharacterInfo>? = null
)

/**
 * 角色纵览
 */
@HiltViewModel
class CharacterSectionViewModel @Inject constructor(
    private val unitRepository: UnitRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterSectionUiState())
    val uiState: StateFlow<CharacterSectionUiState> = _uiState.asStateFlow()

    init {
        getCharacterCount()
        getCharacterInfoList()
    }

    /**
     * 获取角色数量
     */
    private fun getCharacterCount() {
        viewModelScope.launch {
            val count = unitRepository.getCount()
            _uiState.update {
                it.copy(
                    characterCount = count
                )
            }
        }
    }

    /**
     * 获取角色列表
     */
    private fun getCharacterInfoList() {
        viewModelScope.launch {
            try {
                val filterList = unitRepository.getCharacterInfoList(FilterCharacter(), 50)
                _uiState.update {
                    it.copy(
                        characterList = filterList?.subList(0, 10)
                    )
                }
            } catch (e: Exception) {
                LogReportUtil.upload(e, "getCharacterInfoList")
            }

        }
    }

}