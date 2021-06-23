package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import cn.wthee.pcrtool.data.db.dao.TweetDao
import cn.wthee.pcrtool.data.db.entity.TweetData
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.data.paging.TweetRemoteMediator
import cn.wthee.pcrtool.database.AppTweetDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 推特 ViewModel
 */
@HiltViewModel
@ExperimentalPagingApi
class TweetViewModel @Inject constructor(
    private val tweetDao: TweetDao,
    private val database: AppTweetDatabase,
    private val apiRepository: MyAPIRepository
) : ViewModel() {

    private val pageSize = 10
    private val initSize = 20


    var tweetPageList: Flow<PagingData<TweetData>>? = null

    /**
     * 推特数据
     */
    fun getTweet() {
        if (tweetPageList == null) {
            tweetPageList = Pager(
                config = androidx.paging.PagingConfig(
                    pageSize = pageSize,
                    initialLoadSize = initSize,
                    enablePlaceholders = true
                ),
                remoteMediator = TweetRemoteMediator(database, apiRepository)
            ) {
                tweetDao.pagingSource()
            }.flow
        }
    }
}