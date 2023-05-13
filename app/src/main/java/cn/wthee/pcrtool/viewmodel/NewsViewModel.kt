package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import cn.wthee.pcrtool.data.db.dao.NewsDao
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.data.paging.NewsRemoteMediator
import cn.wthee.pcrtool.database.AppNewsDatabase
import cn.wthee.pcrtool.ui.components.DateRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 公告 ViewModel
 */
@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsDao: NewsDao,
    private val database: AppNewsDatabase,
    private val apiRepository: MyAPIRepository
) : ViewModel() {

    private val pageSize = 10

    /**
     * 公告数据
     */
    @OptIn(ExperimentalPagingApi::class)
    fun getNewsPager(region: Int, keyword: String, dateRange: DateRange) = Pager(
        config = PagingConfig(
            pageSize = pageSize
        ),
        remoteMediator = NewsRemoteMediator(
            region,
            keyword,
            dateRange,
            database,
            apiRepository
        )
    ) {
        newsDao.pagingSource(region, keyword)
    }

    /**
     * 获取公告详情
     */
    fun getNewsDetail(id: String) = flow {
        val data = apiRepository.getNewsDetail(id)
        emit(data)
    }

}