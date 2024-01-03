package cn.wthee.pcrtool.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import cn.wthee.pcrtool.data.db.entity.TweetData
import cn.wthee.pcrtool.data.network.ApiRepository
import cn.wthee.pcrtool.database.AppTweetDatabase
import cn.wthee.pcrtool.ui.components.DateRange
import retrofit2.HttpException
import java.io.IOException

/**
 * 推特加载
 */
@OptIn(ExperimentalPagingApi::class)
class TweetRemoteMediator(
    private val keyword: String,
    private val dateRange: DateRange,
    private val database: AppTweetDatabase,
    private val repository: ApiRepository
) : RemoteMediator<Int, TweetData>() {

    private val tweetDao = database.getTweetDao()

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, TweetData>
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
            val response = repository.getTweetList(
                after,
                keyword,
                dateRange.startDate,
                dateRange.endDate
            ).data
            val isEndOfList = response?.isEmpty() ?: false

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    tweetDao.deleteByQuery(keyword)
                }

                //保存到本地
                response?.let {
                    tweetDao.insertAll(it)
                }
            }

            return MediatorResult.Success(
                endOfPaginationReached = isEndOfList
            )
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}