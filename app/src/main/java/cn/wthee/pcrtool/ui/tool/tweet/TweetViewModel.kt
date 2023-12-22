package cn.wthee.pcrtool.ui.tool.tweet

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import cn.wthee.pcrtool.data.db.dao.TweetDao
import cn.wthee.pcrtool.data.db.entity.TweetData
import cn.wthee.pcrtool.data.enums.KeywordType
import cn.wthee.pcrtool.data.model.KeywordData
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.data.paging.TweetRemoteMediator
import cn.wthee.pcrtool.database.AppTweetDatabase
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
data class TweetUiState(
    val pager: Pager<Int, TweetData>? = null,
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
class TweetViewModel @Inject constructor(
    private val tweetDao: TweetDao,
    private val database: AppTweetDatabase,
    private val apiRepository: MyAPIRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TweetUiState())
    val uiState: StateFlow<TweetUiState> = _uiState.asStateFlow()

    private val pageSize = 10

    init {
        getTweetPager("", DateRange())
        getKeywords()
    }

    /**
     * 公告数据
     */
    @OptIn(ExperimentalPagingApi::class)
    private fun getTweetPager(keyword: String, dateRange: DateRange) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    pager = Pager(
                        config = PagingConfig(
                            pageSize = pageSize
                        ),
                        remoteMediator = TweetRemoteMediator(
                            keyword,
                            dateRange,
                            database,
                            apiRepository
                        )
                    ) {
                        tweetDao.pagingSource(keyword)
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
                val data = apiRepository.getKeywords(KeywordType.TWEET.type).data ?: arrayListOf()

                _uiState.update {
                    it.copy(
                        keywordList = data
                    )
                }
            } catch (e: Exception) {
                LogReportUtil.upload(e, "getKeywords#keywordType:${KeywordType.TWEET.type}")
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
        getTweetPager(_uiState.value.keyword, dateRange)
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
        getTweetPager(keyword, _uiState.value.dateRange)
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
        getTweetPager("", DateRange())
    }
}