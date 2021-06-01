package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import cn.wthee.pcrtool.data.db.dao.NewsDao
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.data.paging.NewsRemoteMediator
import cn.wthee.pcrtool.database.AppNewsDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 公告 ViewModel
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

    private var currentRegion = 0
    private var currentSearchResult: Flow<PagingData<NewsTable>>? = null

    val newsList = MutableLiveData<List<NewsTable>>()

    /**
     * 公告数据
     */
    fun getNews(region: Int): Flow<PagingData<NewsTable>> {
        val lastResult = currentSearchResult
        if (region == currentRegion && lastResult != null) {
            return lastResult
        }
        currentRegion = region
        val newResult: Flow<PagingData<NewsTable>> = Pager(
            config = PagingConfig(
                pageSize = pageSize,
                initialLoadSize = initSize,
                enablePlaceholders = true
            ),
            remoteMediator = NewsRemoteMediator(region, database, apiRepository)
        ) {
            newsDao.pagingSource("${region}-%")
        }.flow
        currentSearchResult = newResult
        return newResult
    }

    fun getLocalData(region: Int) {
        viewModelScope.launch {
            val data = newsDao.getNewsList("${region}-%")
            newsList.postValue(data)
        }
    }

}