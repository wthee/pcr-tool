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
@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator(
    private val region: Int,
    private val keyword: String,
    private val database: AppNewsDatabase,
    private val repository: MyAPIRepository
) : RemoteMediator<Int, NewsTable>() {

    private val newsDao = database.getNewsDao()
    private val remoteKeyDao = database.getRemoteKeyDao()

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
                            endOfPaginationReached = true
                        )
                    lastItem.id
                }
            }

            //获取数据
            val response = repository.getNews(region, after, keyword).data
            val isEndOfList = response?.isEmpty() ?: false

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.deleteByQuery("${region}|${keyword}")
                    newsDao.deleteByRegionAndQuery(region, keyword)
                }

                if (response?.isNotEmpty() == true) {
                    //保存远程键
                    remoteKeyDao.insert(
                        RemoteKey(
                            query = "${region}|${keyword}",
                            nextKey = response.last().id
                        )
                    )

                    //保存到本地
                    response.let {
                        newsDao.insertAll(it)
                    }
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