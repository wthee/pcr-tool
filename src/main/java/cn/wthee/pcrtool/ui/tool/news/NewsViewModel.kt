package cn.wthee.pcrtool.ui.tool.news

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.wthee.pcrtool.data.model.NewsRemoteMediator
import cn.wthee.pcrtool.data.model.NewsTable
import cn.wthee.pcrtool.database.NewsDatabase
import kotlinx.coroutines.flow.Flow


class NewsViewModel : ViewModel() {

    lateinit var news: Flow<PagingData<NewsTable>>
    var update = MutableLiveData<Boolean>()
    var loadingMore = MutableLiveData<Boolean>()

    fun getNews(region: Int): Flow<PagingData<NewsTable>> {
        val newsDao = NewsDatabase.getInstance().getNewsDao()
        return Pager(
            config = PagingConfig(pageSize = 50),
            remoteMediator = NewsRemoteMediator(region, NewsDatabase.getInstance())
        ) {
            newsDao.pagingSource("${region}-%")
        }.flow.cachedIn(viewModelScope)
    }


}
