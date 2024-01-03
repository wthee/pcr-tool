package cn.wthee.pcrtool.ui.home.news

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.network.ApiRepository
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.updateLoadingState
import cn.wthee.pcrtool.utils.LogReportUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：公告纵览
 */
@Immutable
data class NewsSectionUiState(
    val newsList: List<NewsTable>? = null,
    val loadingState: LoadingState = LoadingState.Loading
)

/**
 * 公告纵览
 */
@HiltViewModel
class NewsSectionViewModel @Inject constructor(
    private val apiRepository: ApiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsSectionUiState())
    val uiState: StateFlow<NewsSectionUiState> = _uiState.asStateFlow()

    init {
        getNewsOverview()
    }

    /**
     * 获取新闻
     */
    fun getNewsOverview() {
        viewModelScope.launch {
            try {
                val data = apiRepository.getNewsOverviewByRegion()
                val list = data.data
                _uiState.update {
                    it.copy(
                        newsList = list,
                        loadingState = updateLoadingState(list)
                    )
                }
            } catch (e: Exception) {
                LogReportUtil.upload(e, "getNewsOverview")
            }
        }
    }

}