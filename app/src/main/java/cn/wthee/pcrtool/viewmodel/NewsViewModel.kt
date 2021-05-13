package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import cn.wthee.pcrtool.data.db.dao.NewsDao
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.data.paging.NewsRemoteMediator
import cn.wthee.pcrtool.database.AppNewsDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 公告 ViewModel
 *
 * 数据来源 [NewsRemoteMediator]
 */
@HiltViewModel
@ExperimentalPagingApi
class NewsViewModel @Inject constructor(
    private val newsDao: NewsDao,
    private val database: AppNewsDatabase,
    private val apiRepository: MyAPIRepository
) : ViewModel() {


    private val pageSize = 10
    private val initSize = 20


    /**
     * 国服数据
     */
    @ExperimentalPagingApi
    fun getNewsCN(): Flow<PagingData<NewsTable>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize, initialLoadSize = initSize),
            remoteMediator = NewsRemoteMediator(2, database, apiRepository)
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
            remoteMediator = NewsRemoteMediator(3, database, apiRepository)
        ) {
            newsDao.pagingSource("${3}-%")
        }.flow
    }

    /**
     * 日服数据
     */
    @ExperimentalPagingApi
    fun getNewsJP(): Flow<PagingData<NewsTable>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize, initialLoadSize = initSize),
            remoteMediator = NewsRemoteMediator(4, database, apiRepository)
        ) {
            newsDao.pagingSource("${4}-%")
        }.flow
    }

}
