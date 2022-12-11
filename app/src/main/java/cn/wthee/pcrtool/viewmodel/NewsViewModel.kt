package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import cn.wthee.pcrtool.data.db.dao.NewsDao
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.data.paging.NewsRemoteMediator
import cn.wthee.pcrtool.database.AppNewsDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val newsDetail = MutableLiveData<ResponseData<NewsTable>>()

    /**
     * 公告数据
     */
    @OptIn(ExperimentalPagingApi::class)
    fun getNewsPager(region: Int, keyword: String) = Pager(
        config = PagingConfig(
            pageSize = pageSize
        ),
        remoteMediator = NewsRemoteMediator(
            region,
            keyword,
            database,
            apiRepository
        )
    ) {
        newsDao.pagingSource(region, keyword)
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