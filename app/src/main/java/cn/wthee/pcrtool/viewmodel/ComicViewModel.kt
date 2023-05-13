package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import cn.wthee.pcrtool.data.db.dao.ComicDao
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.data.paging.ComicRemoteMediator
import cn.wthee.pcrtool.database.AppComicDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 漫画 ViewModel
 */
@HiltViewModel
class ComicViewModel @Inject constructor(
    private val comicDao: ComicDao,
    private val database: AppComicDatabase,
    private val apiRepository: MyAPIRepository
) : ViewModel() {

    /**
     * 推特数据
     */
    @OptIn(ExperimentalPagingApi::class)
    fun getComic(keyword: String) = Pager(
        config = PagingConfig(
            pageSize = 1000
        ),
        remoteMediator = ComicRemoteMediator(
            keyword,
            database,
            apiRepository
        )
    ) {
        comicDao.pagingSource(keyword)
    }

}