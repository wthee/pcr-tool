package cn.wthee.pcrtool.ui.home.tool

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.preferences.MainPreferencesKeys
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.dataStoreMain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * 页面状态：功能纵览
 */
@Immutable
data class ToolSectionUiState(
    val toolOrderData: String? = null,
    val loadingState: LoadingState = LoadingState.Loading
)

/**
 * 功能纵览
 */
@HiltViewModel
class ToolSectionViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ToolSectionUiState())
    val uiState: StateFlow<ToolSectionUiState> = _uiState.asStateFlow()

    /**
     * 获取功能排序
     */
    fun getToolOrderData() {
        val orderData = runBlocking {
            val data = MyApplication.context.dataStoreMain.data.first()
            data[MainPreferencesKeys.SP_TOOL_ORDER] ?: ""
        }
        _uiState.update {
            it.copy(
                toolOrderData = orderData,
                loadingState = it.loadingState.isSuccess(orderData != "")
            )
        }
    }

    /**
     * 更新排序
     */
    fun updateOrderData(orderData: String) {
        _uiState.update {
            it.copy(
                toolOrderData = orderData,
                loadingState = it.loadingState.isSuccess(orderData != "")
            )
        }
    }
}