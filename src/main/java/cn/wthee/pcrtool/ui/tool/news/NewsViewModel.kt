package cn.wthee.pcrtool.ui.tool.news

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.wthee.pcrtool.data.model.News
import cn.wthee.pcrtool.data.model.NewsDataPagingSource
import cn.wthee.pcrtool.data.model.NewsRemoteMediator
import cn.wthee.pcrtool.data.model.NewsTable
import cn.wthee.pcrtool.database.NewsDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class NewsViewModel : ViewModel() {

    lateinit var news: Flow<PagingData<News>>
    lateinit var newsTest: Flow<PagingData<NewsTable>>
    var update = MutableLiveData<Boolean>()
    var loadingMore = MutableLiveData<Boolean>()

    //获取装备列表
    fun getNews(region: Int) {
        loadingMore.postValue(true)
        viewModelScope.launch {
            news = Pager(PagingConfig(pageSize = 10)) {
                NewsDataPagingSource(this@NewsViewModel, region)
            }.flow.cachedIn(viewModelScope)
            update.postValue(true)
        }
    }

    fun getNewsAndSave(region: Int) {
        val newsDao = NewsDatabase.getInstance().getNewsDao()
        newsTest = Pager(
            config = PagingConfig(pageSize = 50),
            remoteMediator = NewsRemoteMediator(region, NewsDatabase.getInstance())
        ) {
            newsDao.pagingSource("${region}-%")
        }.flow
        update.postValue(true)
    }
}
