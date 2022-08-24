package cn.wthee.pcrtool.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import cn.wthee.pcrtool.data.db.entity.RemoteKey
import cn.wthee.pcrtool.data.db.entity.TweetData
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.database.AppTweetDatabase
import retrofit2.HttpException
import java.io.IOException

/**
 * 推特加载
 */
@OptIn(ExperimentalPagingApi::class)
class TweetRemoteMediator(
    private val keyword: String,
    private val database: AppTweetDatabase,
    private val repository: MyAPIRepository
) : RemoteMediator<Int, TweetData>() {

    private val tweetDao = database.getTweetDao()
    private val remoteKeyDao = database.getRemoteKeyDao()

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
                            endOfPaginationReached = true
                        )
                    lastItem.id
                }
            }

            //获取数据
            val response = repository.getTweet(after, keyword).data
            val isEndOfList = response?.isEmpty() ?: false

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.deleteByQuery(keyword)
                    tweetDao.deleteByQuery(keyword)
                }

                //保存远程键
                remoteKeyDao.insert(
                    RemoteKey(
                        query = keyword,
                        nextKey = response?.last()?.id
                    )
                )

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