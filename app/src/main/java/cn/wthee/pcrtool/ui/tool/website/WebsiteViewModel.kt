package cn.wthee.pcrtool.ui.tool.website

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.data.model.WebsiteGroupData
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.ui.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：网站
 */
@Immutable
data class WebsiteUiState(
    val websiteResponseData: ResponseData<List<WebsiteGroupData>>? = null,
    val type: Int = 0,
    val openDialog: Boolean = false,
    val loadingState: LoadingState = LoadingState.Loading
)

/**
 * 网站 ViewModel
 *
 * @param apiRepository
 */
@HiltViewModel
class WebsiteViewModel @Inject constructor(
    private val apiRepository: MyAPIRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WebsiteUiState())
    val uiState: StateFlow<WebsiteUiState> = _uiState.asStateFlow()

    init {
        getWebsiteList()
    }

    /**
     * 获取网站列表
     */
    private fun getWebsiteList() {
        viewModelScope.launch {
            val responseData = apiRepository.getWebsiteList()
            _uiState.update {
                it.copy(
                    websiteResponseData = responseData,
                    loadingState = it.loadingState.isSuccess(responseData.data != null)
                )
            }
        }
    }


    /**
     * 切换类型
     */
    fun changeSelect(type: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    type = type
                )
            }
        }
    }

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
}
