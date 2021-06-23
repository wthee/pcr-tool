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
@ExperimentalPagingApi
class TweetRemoteMediator(
    private val database: AppTweetDatabase,
    private val repository: MyAPIRepository
) : RemoteMediator<Int, TweetData>() {

    private val tweetDao = database.getTweetDao()
    private val remoteKeyDao = database.getRemoteKeyDao()
    private val pageDefaultIndex = 1
    private var currPage = 1

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, TweetData>
    ): MediatorResult {
        try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKey = state.anchorPosition?.let { position ->
                        state.closestItemToPosition(position)?.id?.let { repoId ->
                            remoteKeyDao.remoteKeys(repoId)
                        }
                    }
                    remoteKey?.nextKey?.minus(pageDefaultIndex) ?: pageDefaultIndex
                }
                LoadType.PREPEND -> {
                    val remoteKey = database.withTransaction {
                        val key = state.firstItemOrNull()?.id
                        if (key == null) null else remoteKeyDao.remoteKeys(key)
                    }
                    if (remoteKey?.prevKey == null) {
                        return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    }
                    remoteKey.prevKey
                }
                LoadType.APPEND -> {
                    val key = state.lastItemOrNull()?.id
                    val remoteKey =
                        if (key == null) null else remoteKeyDao.remoteKeys(key)
                    if (remoteKey?.nextKey == null) {
                        currPage + 1
//                        return MediatorResult.Success(
//                            endOfPaginationReached = true
//                        )
                    } else {
                        remoteKey.nextKey
                    }
                }
            }
            currPage = page
            val response = repository.getTweet(page).data
            val list = response ?: arrayListOf()

            val isEndOfList = response?.isEmpty() ?: false
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.clearAllRemoteKeys()
                    tweetDao.clearAll()
                }
                val prevKey = if (page == pageDefaultIndex) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val keys = list.map {
                    RemoteKey(repoId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                remoteKeyDao.insertAll(keys)
                tweetDao.insertAll(list)
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