package cn.wthee.pcrtool.ui.tool.storyevent

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 页面状态：剧情活动boss详情
 */
@Immutable
data class StoryEventBossUiState(
    //当前mode
    val modeIndex: Int = 0,
    val openDialog: Boolean = false
)

/**
 * 剧情活动boss详情 ViewModel
 */
@HiltViewModel
class StoryEventBossViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(StoryEventBossUiState())
    val uiState: StateFlow<StoryEventBossUiState> = _uiState.asStateFlow()


    /**
     * 弹窗状态更新
     */
    fun changeDialog(openDialog: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    openDialog = openDialog
                )
            }
        }
    }

    /**
     * 切换选择
     */
    fun changeSelect(modeIndex: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    modeIndex = modeIndex
                )
            }
        }
    }
}
