package cn.wthee.pcrtool.ui.tool.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.network.model.NewsRemoteMediator
import cn.wthee.pcrtool.database.AppNewsDatabase
import kotlinx.coroutines.flow.Flow

/**
 * 公告 ViewModel
 *
 * 数据来源 [NewsRemoteMediator]
 */
class NewsViewModel : ViewModel() {

    private val newsDao = AppNewsDatabase.getInstance().getNewsDao()

    private val pageSize = 10
    private val initSize = 20

    /**
     * 国服数据
     */
    @ExperimentalPagingApi
    fun getNewsCN(): Flow<PagingData<NewsTable>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize, initialLoadSize = initSize),
            remoteMediator = NewsRemoteMediator(2, AppNewsDatabase.getInstance())
        ) {
            newsDao.pagingSource("${2}-%")
        }.flow.cachedIn(viewModelScope)
    }

    /**
     * 台服数据
     */
    @ExperimentalPagingApi
    fun getNewsTW(): Flow<PagingData<NewsTable>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize, initialLoadSize = initSize),
            remoteMediator = NewsRemoteMediator(3, AppNewsDatabase.getInstance())
        ) {
            newsDao.pagingSource("${3}-%")
        }.flow.cachedIn(viewModelScope)
    }

    /**
     * 日服数据
     */
    @ExperimentalPagingApi
    fun getNewsJP(): Flow<PagingData<NewsTable>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize, initialLoadSize = initSize),
            remoteMediator = NewsRemoteMediator(4, AppNewsDatabase.getInstance())
        ) {
            newsDao.pagingSource("${4}-%")
        }.flow.cachedIn(viewModelScope)
    }


}
