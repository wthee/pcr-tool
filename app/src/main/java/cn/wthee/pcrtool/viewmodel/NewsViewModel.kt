package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
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

    var newsPageList: Flow<PagingData<NewsTable>>? = null

    /**
     * 公告数据
     */
    fun getNews(region: Int) {
        if (newsPageList == null || region != currentRegion) {
            currentRegion = region
            newsPageList = Pager(
                config = androidx.paging.PagingConfig(
                    pageSize = pageSize,
                    initialLoadSize = initSize,
                    enablePlaceholders = true
                ),
                remoteMediator = NewsRemoteMediator(region, database, apiRepository)
            ) {
                newsDao.pagingSource("${region}-%")
            }.flow
        }

    }
}