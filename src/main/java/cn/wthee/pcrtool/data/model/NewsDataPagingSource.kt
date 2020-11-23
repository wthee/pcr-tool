package cn.wthee.pcrtool.data.model

import androidx.paging.PagingSource
import cn.wthee.pcrtool.data.MyAPIRepository

class NewsDataPagingSource(
    private val region: Int
) : PagingSource<Int, News>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, News> {
        try {
            val nextPage = params.key ?: 1
            val response = MyAPIRepository.getNewsCall(region, nextPage)
            return LoadResult.Page(
                data = response,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = nextPage + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}