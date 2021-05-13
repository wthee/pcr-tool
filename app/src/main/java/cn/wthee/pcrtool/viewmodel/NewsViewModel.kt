package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import cn.wthee.pcrtool.data.db.dao.NewsDao
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.paging.NewsRemoteMediator
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
    private val remoteMediator: NewsRemoteMediator
) : ViewModel() {


    private val pageSize = 10
    private val initSize = 20


    /**
     * 国服数据
     */
    @ExperimentalPagingApi
    fun getNewsCN(): Flow<PagingData<NewsTable>> {
        remoteMediator.setRegion(2)
        return Pager(
            config = PagingConfig(pageSize = pageSize, initialLoadSize = initSize),
            remoteMediator = remoteMediator
        ) {
            newsDao.pagingSource("${2}-%")
        }.flow.cachedIn(viewModelScope)
    }

    /**
     * 台服数据
     */
    @ExperimentalPagingApi
    fun getNewsTW(): Flow<PagingData<NewsTable>> {
        remoteMediator.setRegion(3)
        return Pager(
            config = PagingConfig(pageSize = pageSize, initialLoadSize = initSize),
            remoteMediator = remoteMediator
        ) {
            newsDao.pagingSource("${3}-%")
        }.flow
    }

    /**
     * 日服数据
     */
    @ExperimentalPagingApi
    fun getNewsJP(): Flow<PagingData<NewsTable>> {
        remoteMediator.setRegion(4)
        return Pager(
            config = PagingConfig(pageSize = pageSize, initialLoadSize = initSize),
            remoteMediator = remoteMediator
        ) {
            newsDao.pagingSource("${4}-%")
        }.flow
    }

}
