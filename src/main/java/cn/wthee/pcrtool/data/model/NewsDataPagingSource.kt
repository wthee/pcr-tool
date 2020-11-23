package cn.wthee.pcrtool.data.model

import android.util.Log
import androidx.paging.PagingSource
import cn.wthee.pcrtool.data.MyAPIRepository
import cn.wthee.pcrtool.ui.tool.news.NewsViewModel

class NewsDataPagingSource(
    private val viewModel: NewsViewModel,
    private val region: Int
) : PagingSource<Int, News>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, News> {
        return try {
            val nextPage = params.key ?: 1
            Log.e("next", "$region:$nextPage")
            viewModel.loadingMore.postValue(true)
            val response = MyAPIRepository.getNewsCall(region, nextPage)
            viewModel.loadingMore.postValue(false)
            LoadResult.Page(
                data = response,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = nextPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}