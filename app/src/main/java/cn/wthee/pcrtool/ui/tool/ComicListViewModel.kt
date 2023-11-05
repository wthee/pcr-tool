package cn.wthee.pcrtool.ui.tool

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import cn.wthee.pcrtool.data.db.dao.ComicDao
import cn.wthee.pcrtool.data.db.entity.ComicData
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.data.paging.ComicRemoteMediator
import cn.wthee.pcrtool.database.AppComicDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：漫画
 */
@Immutable
data class ComicListUiState(
    val pager: Pager<Int, ComicData>? = null,
    //选中的目录下标
    val selectedIndex: Int = 0,
    val openDialog: Boolean = false
)

/**
 * 漫画 ViewModel
 */
@HiltViewModel
class ComicListViewModel @Inject constructor(
    private val comicDao: ComicDao,
    private val database: AppComicDatabase,
    private val apiRepository: MyAPIRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComicListUiState())
    val uiState: StateFlow<ComicListUiState> = _uiState.asStateFlow()

    init {
        getComic()
    }

    /**
     * 弹窗状态更新
     */
    fun changeDialog(openDialog: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    openDialog = openDialog
                )
            }
        }
    }

    /**
     * 切换选择
     */
    fun changeSelect(selectedIndex: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    selectedIndex = selectedIndex
                )
            }
        }
    }

    /**
     * 漫画数据
     */
    @OptIn(ExperimentalPagingApi::class)
    private fun getComic() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    pager = Pager(
                        config = PagingConfig(
                            pageSize = 1000
                        ),
                        remoteMediator = ComicRemoteMediator(
                            "",
                            database,
                            apiRepository
                        )
                    ) {
                        comicDao.pagingSource("")
                    }
                )
            }
        }
    }

}