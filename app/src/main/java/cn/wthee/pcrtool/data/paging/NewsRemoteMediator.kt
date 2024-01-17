package cn.wthee.pcrtool.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.network.ApiRepository
import cn.wthee.pcrtool.database.AppNewsDatabase
import cn.wthee.pcrtool.ui.components.DateRange

/**
 * 公告加载
 */
@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator(
    private val region: Int,
    private val keyword: String,
    private val dateRange: DateRange,
    private val database: AppNewsDatabase,
    private val repository: ApiRepository
) : RemoteMediator<Int, NewsTable>() {

    private val newsDao = database.getNewsDao()

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, NewsTable>
    ): MediatorResult {
        try {
            val after = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = false
                        )
                    lastItem.id
                }
            }

            //获取数据
            val response = repository.getNewsList(
                region,
                after,
                keyword,
                dateRange.startDate,
                dateRange.endDate
            ).data
            val isEndOfList = response?.isEmpty() ?: false

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    newsDao.deleteByRegionAndQuery(region, keyword)
                }

                //保存到本地
                response?.let {
                    newsDao.insertAll(it)
                }
            }

            return MediatorResult.Success(
                endOfPaginationReached = isEndOfList
            )
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}