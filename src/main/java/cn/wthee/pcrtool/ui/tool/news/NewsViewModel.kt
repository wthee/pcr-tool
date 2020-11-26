package cn.wthee.pcrtool.ui.tool.news

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.wthee.pcrtool.data.entity.NewsTable
import cn.wthee.pcrtool.data.model.NewsRemoteMediator
import cn.wthee.pcrtool.database.AppNewsDatabase
import kotlinx.coroutines.flow.Flow


class NewsViewModel : ViewModel() {

    lateinit var news: Flow<PagingData<NewsTable>>
    var loadingMore = MutableLiveData<Boolean>()

    fun getNewsCN(): Flow<PagingData<NewsTable>> {
        val newsDao = AppNewsDatabase.getInstance().getNewsDao()
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = NewsRemoteMediator(2, AppNewsDatabase.getInstance())
        ) {
            newsDao.pagingSource("${2}-%")
        }.flow.cachedIn(viewModelScope)
    }

    fun getNewsTW(): Flow<PagingData<NewsTable>> {
        val newsDao = AppNewsDatabase.getInstance().getNewsDao()
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = NewsRemoteMediator(3, AppNewsDatabase.getInstance())
        ) {
            newsDao.pagingSource("${3}-%")
        }.flow.cachedIn(viewModelScope)
    }

    fun getNewsJP(): Flow<PagingData<NewsTable>> {
        val newsDao = AppNewsDatabase.getInstance().getNewsDao()
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = NewsRemoteMediator(4, AppNewsDatabase.getInstance())
        ) {
            newsDao.pagingSource("${4}-%")
        }.flow.cachedIn(viewModelScope)
    }


}