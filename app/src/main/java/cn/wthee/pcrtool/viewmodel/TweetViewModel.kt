package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import cn.wthee.pcrtool.data.db.dao.TweetDao
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.data.paging.TweetRemoteMediator
import cn.wthee.pcrtool.database.AppTweetDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 推特 ViewModel
 */
@HiltViewModel
class TweetViewModel @Inject constructor(
    private val tweetDao: TweetDao,
    private val database: AppTweetDatabase,
    private val apiRepository: MyAPIRepository
) : ViewModel() {

    private val pageSize = 10

    /**
     * 推特数据
     */
    @OptIn(ExperimentalPagingApi::class)
    fun getTweet(keyword: String) = Pager(
        config = PagingConfig(
            pageSize = pageSize
        ),
        remoteMediator = TweetRemoteMediator(keyword, database, apiRepository)
    ) {
        tweetDao.pagingSource(keyword)
    }
}