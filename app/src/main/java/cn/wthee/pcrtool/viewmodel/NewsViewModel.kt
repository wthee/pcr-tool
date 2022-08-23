package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import cn.wthee.pcrtool.data.db.dao.NewsDao
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.model.ResponseData
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
class NewsViewModel @Inject constructor(
    private val newsDao: NewsDao,
    private val database: AppNewsDatabase,
    private val apiRepository: MyAPIRepository
) : ViewModel() {

    private val pageSize = 10
    private val initSize = 20


    var newsPageList0: Flow<PagingData<NewsTable>>? = null
    var newsPageList1: Flow<PagingData<NewsTable>>? = null
    var newsPageList2: Flow<PagingData<NewsTable>>? = null
    val newsDetail = MutableLiveData<ResponseData<NewsTable>>()

    /**
     * 公告数据
     */
    @OptIn(ExperimentalPagingApi::class)
    fun getNews(region: Int, keyword: String) {
        when (region) {
            2 -> {
                if (newsPageList0 == null) {
                    newsPageList0 = Pager(
                        config = androidx.paging.PagingConfig(
                            pageSize = pageSize,
                            initialLoadSize = initSize,
                            enablePlaceholders = true
                        ),
                        remoteMediator = NewsRemoteMediator(
                            region,
                            keyword,
                            database,
                            apiRepository
                        )
                    ) {
                        newsDao.pagingSource("${region}-%")
                    }.flow
                }
            }
            3 -> {
                if (newsPageList1 == null) {
                    newsPageList1 = Pager(
                        config = androidx.paging.PagingConfig(
                            pageSize = pageSize,
                            initialLoadSize = initSize,
                            enablePlaceholders = true
                        ),
                        remoteMediator = NewsRemoteMediator(
                            region,
                            keyword,
                            database,
                            apiRepository
                        )
                    ) {
                        newsDao.pagingSource("${region}-%")
                    }.flow
                }
            }
            else -> {
                if (newsPageList2 == null) {
                    newsPageList2 = Pager(
                        config = androidx.paging.PagingConfig(
                            pageSize = pageSize,
                            initialLoadSize = initSize,
                            enablePlaceholders = true
                        ),
                        remoteMediator = NewsRemoteMediator(
                            region,
                            keyword,
                            database,
                            apiRepository
                        )
                    ) {
                        newsDao.pagingSource("${region}-%")
                    }.flow
                }
            }
        }
    }

    /**
     * 获取公告详情
     */
    fun getNewsDetail(id: String) {
        viewModelScope.launch {
            if (newsDetail.value == null || newsDetail.value!!.status != 0) {
                val data = apiRepository.getNewsDetail(id)
                newsDetail.postValue(data)
            }
        }
    }
}