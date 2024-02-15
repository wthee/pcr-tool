package cn.wthee.pcrtool.ui.tool.news

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import cn.wthee.pcrtool.data.db.dao.NewsDao
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.enums.KeywordType
import cn.wthee.pcrtool.data.model.KeywordData
import cn.wthee.pcrtool.data.network.ApiRepository
import cn.wthee.pcrtool.data.paging.NewsRemoteMediator
import cn.wthee.pcrtool.database.AppNewsDatabase
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.components.DateRange
import cn.wthee.pcrtool.utils.LogReportUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 页面状态：公告
 */
@Immutable
data class NewsUiState(
    val pager: Pager<Int, NewsTable>? = null,
    //日期
    val dateRange: DateRange = DateRange(),
    //日期选择弹窗
    val openDialog: Boolean = false,
    //搜索弹窗
    val openSearch: Boolean = false,
    //快捷搜索关键词
    val keywordList: List<KeywordData> = emptyList(),
    val keyword: String = ""
)


/**
 * 公告 ViewModel
 */
@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsDao: NewsDao,
    private val database: AppNewsDatabase,
    private val apiRepository: ApiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    private val pageSize = 10

    init {
        getNewsPager("", DateRange())
        getKeywords()
    }

    /**
     * 公告数据
     */
    @OptIn(ExperimentalPagingApi::class)
    private fun getNewsPager(keyword: String, dateRange: DateRange) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    pager = Pager(
                        config = PagingConfig(
                            pageSize = pageSize
                        ),
                        remoteMediator = NewsRemoteMediator(
                            keyword = keyword,
                            dateRange = dateRange,
                            database = database,
                            repository = apiRepository
                        )
                    ) {
                        newsDao.pagingSource(MainActivity.regionType.value, keyword)

                    }
                )
            }
        }
    }

    /**
     * 获取关键词
     */
    private fun getKeywords() {
        viewModelScope.launch {
            try {
                val data = apiRepository.getKeywords(KeywordType.NEWS.type).data ?: arrayListOf()

                _uiState.update {
                    it.copy(
                        keywordList = data
                    )
                }
            } catch (e: Exception) {
                LogReportUtil.upload(e, "getKeywords#keywordType:${KeywordType.NEWS.type}")
            }
        }
    }

    /**
     * 日期选择更新
     */
    fun changeRange(dateRange: DateRange) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    dateRange = dateRange
                )
            }
        }
        getNewsPager(_uiState.value.keyword, dateRange)
    }


    /**
     * 关键词更新
     */
    fun changeKeyword(keyword: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    keyword = keyword
                )
            }
        }
        getNewsPager(keyword, _uiState.value.dateRange)
    }

    /**
     * 弹窗状态更新
     */
    fun changeDialog(openDialog: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    openDialog = openDialog,
                    openSearch = false
                )
            }
        }
    }

    /**
     * 搜索弹窗
     */
    fun changeSearchBar(openSearch: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    openSearch = openSearch,
                    openDialog = false
                )
            }
        }
    }

    /**
     * 重置
     */
    fun reset() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    dateRange = DateRange(),
                    keyword = ""
                )
            }
        }
        getNewsPager("", DateRange())
    }
}