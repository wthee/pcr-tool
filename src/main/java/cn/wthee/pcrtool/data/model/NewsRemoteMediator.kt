package cn.wthee.pcrtool.data.model

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import cn.wthee.pcrtool.data.MyAPIRepository
import cn.wthee.pcrtool.data.entity.NewsTable
import cn.wthee.pcrtool.data.entity.RemoteKey
import cn.wthee.pcrtool.database.AppNewsDatabase
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator(
    private val region: Int,
    private val database: AppNewsDatabase,
) : RemoteMediator<Int, NewsTable>() {

    private val pageIndex = 1

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, NewsTable>
    ): MediatorResult {

        val page = when (val pageKeyData = getKeyPageData(loadType, state)) {
            is MediatorResult.Success -> {
                return pageKeyData
            }
            else -> {
                pageKeyData as Int
            }
        }

        try {
            val response = MyAPIRepository.getNews(region, page).data
            val list = arrayListOf<NewsTable>()
            response?.forEach {
                list.add(NewsTable("${region}-${it.id}", it.title, it.getTags(), it.url, it.date))
            }
            val isEndOfList = response?.isEmpty() ?: true
            database.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    database.getRemoteKeyDao().clearRemoteKeys("${region}-%")
                    database.getNewsDao().clearAll("${region}-%")
                }
                val prevKey = if (page == pageIndex) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val keys = list.map {
                    RemoteKey(repoId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.getRemoteKeyDao().insertAll(keys)
                database.getNewsDao().insertAll(list)
            }
            return MediatorResult.Success(endOfPaginationReached = isEndOfList)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    /**
     * this returns the page key or the final end of list success result
     */
    private suspend fun getKeyPageData(
        loadType: LoadType,
        state: PagingState<Int, NewsTable>
    ): Any? {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getClosestRemoteKey(state)
                remoteKeys?.nextKey?.minus(1) ?: pageIndex
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                    ?: throw InvalidObjectException("Remote key should not be null for $loadType")
                remoteKeys.nextKey
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                    ?: throw InvalidObjectException("Invalid state, key should not be null")
                //end of list condition reached
                remoteKeys.prevKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                remoteKeys.prevKey
            }
        }
    }

    /**
     * get the last remote key inserted which had the data
     */
    private suspend fun getLastRemoteKey(state: PagingState<Int, NewsTable>): RemoteKey? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let { news -> database.getRemoteKeyDao().remoteKeys(news.id) }
    }

    /**
     * get the first remote key inserted which had the data
     */
    private suspend fun getFirstRemoteKey(state: PagingState<Int, NewsTable>): RemoteKey? {
        return state.pages
            .firstOrNull { it.data.isNotEmpty() }
            ?.data?.firstOrNull()
            ?.let { news -> database.getRemoteKeyDao().remoteKeys(news.id) }
    }

    /**
     * get the closest remote key inserted which had the data
     */
    private suspend fun getClosestRemoteKey(state: PagingState<Int, NewsTable>): RemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                database.getRemoteKeyDao().remoteKeys(repoId)
            }
        }
    }
}