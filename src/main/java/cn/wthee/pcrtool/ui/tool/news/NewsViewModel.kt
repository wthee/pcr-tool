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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class NewsViewModel : ViewModel() {

    lateinit var news: Flow<PagingData<News>>
    var update = MutableLiveData<Boolean>()

    //获取装备列表
    fun getNews(region: Int) {
        viewModelScope.launch {
            news = Pager(PagingConfig(pageSize = 10)) {
                NewsDataPagingSource(region)
            }.flow.cachedIn(viewModelScope)
            update.postValue(true)
        }
    }
}
