package cn.wthee.pcrtool.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import cn.wthee.pcrtool.data.db.entity.ComicData
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.database.AppComicDatabase
import retrofit2.HttpException
import java.io.IOException

/**
 * 漫画加载
 * 漫画一次请求全部加载，仅实现缓存
 */
@OptIn(ExperimentalPagingApi::class)
class ComicRemoteMediator(
    private val keyword: String,
    private val database: AppComicDatabase,
    private val repository: MyAPIRepository
) : RemoteMediator<Int, ComicData>() {

    private val comicDao = database.getComicDao()

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, ComicData>
    ): MediatorResult {
        try {
            val after = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.APPEND, LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
            }

            //获取数据
            val response = repository.getComic(
                after,
                keyword
            ).data
            val isEndOfList = response?.isEmpty() ?: false

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    comicDao.deleteByQuery(keyword)
                }

                //保存到本地
                response?.let {
                    comicDao.insertAll(it)
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