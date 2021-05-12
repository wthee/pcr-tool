package cn.wthee.pcrtool.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.db.entity.RemoteKey
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.database.AppNewsDatabase
import retrofit2.HttpException
import java.io.IOException

/**
 * 公告加载
 */
@ExperimentalPagingApi
class NewsRemoteMediator(
    private val region: Int,
    private val database: AppNewsDatabase,
) : RemoteMediator<Int, NewsTable>() {

    private val newsDao = database.getNewsDao()
    private val remoteKeyDao = database.getRemoteKeyDao()
    private val pageDefaultIndex = 1
    private var currPage = 1

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, NewsTable>
    ): MediatorResult {
        try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKey = state.anchorPosition?.let { position ->
                        state.closestItemToPosition(position)?.id?.let { repoId ->
                            database.getRemoteKeyDao().remoteKeys(repoId)
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
                    val remoteKey = if (key == null) null else remoteKeyDao.remoteKeys(key)
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
            val response = MyAPIRepository.getInstance().getNews(region, page).data
            val list = arrayListOf<NewsTable>()
            response?.forEach {
                list.add(
                    NewsTable(
                        "${region}-${it.id}-${it.date}",
                        it.title,
                        it.tags,
                        it.url,
                        it.date
                    )
                )
            }
            val isEndOfList = response?.isEmpty() ?: false
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.clearRemoteKeys("${region}-%")
                    newsDao.clearAll("${region}-%")
                }
                val prevKey = if (page == pageDefaultIndex) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val keys = list.map {
                    RemoteKey(repoId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                remoteKeyDao.insertAll(keys)
                newsDao.insertAll(list)
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